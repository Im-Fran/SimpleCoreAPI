package cl.franciscosolis.simplecoreapi.paper.modules.command

import cl.franciscosolis.simplecoreapi.module.Module
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.command.SimpleCommandMap

/**
 * Command registration on Paper, without touching the `paper-plugin.yml` of the plugin that
 * registers them.
 *
 * It offers two levels:
 *
 * 1. [registerCommand], with the same signature as on Spigot and BungeeCord, for simple
 *    commands that take raw arguments. Works at any time.
 * 2. [registerBrigadier], Paper-only, for typed argument trees with native client-side
 *    suggestions.
 *
 * ```kotlin
 * val commands = requireModule<CommandModule>()
 *
 * commands.registerCommand("login", aliases = listOf("l"), permission = "simpleauth.login") { sender, args ->
 *     sender.sendPlainMessage("Usage: /login <password>")
 * }
 *
 * commands.registerBrigadier { registrar ->
 *     registrar.register(
 *         Commands.literal("kit")
 *             .then(Commands.argument("name", StringArgumentType.word())
 *                 .executes { ctx -> 1 })
 *             .build(),
 *     )
 * }
 * ```
 */
public class CommandModule : Module {

    private val registered = LinkedHashMap<String, Command>()

    /**
     * Registers a simple command. Same signature as on Spigot and BungeeCord.
     *
     * @param permission permission required to run it, or `null` to allow anyone.
     *   Bukkit checks it before calling [executor].
     * @param tabCompleter completion suggestions, or `null` to offer none.
     * @return the registered command, usable with [unregisterCommand].
     */
    public fun registerCommand(
        name: String,
        aliases: List<String> = emptyList(),
        permission: String? = null,
        description: String = "",
        usage: String = "/$name",
        tabCompleter: ((sender: CommandSender, args: Array<String>) -> List<String>)? = null,
        executor: (sender: CommandSender, args: Array<String>) -> Unit,
    ): Command {
        unregisterCommand(name)

        val command = object : Command(name, description, usage, aliases) {
            override fun execute(sender: CommandSender, label: String, args: Array<String>): Boolean {
                executor(sender, args)
                return true
            }

            override fun tabComplete(
                sender: CommandSender,
                alias: String,
                args: Array<String>,
            ): List<String> = tabCompleter?.invoke(sender, args) ?: emptyList()
        }
        command.permission = permission

        commandMap.register(FALLBACK_PREFIX, command)
        registered[name.lowercase()] = command
        refreshClients()
        return command
    }

    /**
     * Registers commands through Paper's Brigadier API.
     *
     * [builder] is invoked with Paper's registrar on every `COMMANDS` event, so the commands
     * survive a datapack reload.
     *
     * Call it from your plugin's `onEnable`: the event is fired after plugins are enabled.
     * If you call it later, the commands will not show up until the next datapack reload;
     * use [registerCommand] for those cases.
     */
    public fun registerBrigadier(builder: (registrar: Commands) -> Unit) {
        BrigadierCommands.add(builder)
    }

    /** Removes a command registered with [registerCommand]. @return `true` if it existed. */
    public fun unregisterCommand(name: String): Boolean {
        val command = registered.remove(name.lowercase()) ?: return false
        remove(command)
        refreshClients()
        return true
    }

    /** Names of the commands registered with [registerCommand]. */
    public val commands: Set<String> get() = registered.keys.toSet()

    override fun onDisable() {
        registered.values.forEach { remove(it) }
        registered.clear()
        BrigadierCommands.clear()
    }

    private fun remove(command: Command) {
        command.unregister(commandMap)
        // unregister() leaves the entry behind, so the command would still be dispatched.
        val map = knownCommands ?: return
        // Paper forwards this map to the Brigadier dispatcher: it accepts remove(key) but not
        // mutable iteration, so the keys are copied before deleting them.
        map.filterValues { it == command }.keys.forEach { map.remove(it) }
    }

    /** Without this the client keeps suggesting the previous command list. */
    private fun refreshClients() {
        Bukkit.getOnlinePlayers().forEach { it.updateCommands() }
    }

    /** Paper exposes the CommandMap as API, unlike Spigot. */
    private val commandMap: CommandMap get() = Bukkit.getCommandMap()

    /** Paper's public getter hands back an immutable view, so go for the real field. */
    @Suppress("UNCHECKED_CAST")
    private val knownCommands: MutableMap<String, Command>? by lazy {
        runCatching {
            SimpleCommandMap::class.java.getDeclaredField("knownCommands")
                .apply { isAccessible = true }
                .get(commandMap) as MutableMap<String, Command>
        }.getOrNull()
    }

    private companion object {
        /** Prefix Bukkit uses for the `simplecoreapi:<command>` aliases. */
        private const val FALLBACK_PREFIX = "simplecoreapi"
    }
}
