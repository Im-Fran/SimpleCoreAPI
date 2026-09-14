package cl.franciscosolis.simplecoreapi.spigot.modules.command

import cl.franciscosolis.simplecoreapi.module.Module
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.command.CommandSender
import org.bukkit.command.SimpleCommandMap

/**
 * Command registration on Spigot, without touching the `plugin.yml` of the plugin that
 * registers them.
 *
 * ```kotlin
 * val commands = requireModule<CommandModule>()
 * commands.registerCommand("login", aliases = listOf("l"), permission = "simpleauth.login") { sender, args ->
 *     sender.sendMessage("Usage: /login <password>")
 * }
 * ```
 *
 * Commands can be registered at any time, including long after the server has started.
 */
public class CommandModule : Module {

    private val registered = LinkedHashMap<String, Command>()

    /**
     * Registers a command.
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

    /** Removes a command registered by this module. @return `true` if it existed. */
    public fun unregisterCommand(name: String): Boolean {
        val command = registered.remove(name.lowercase()) ?: return false
        remove(command)
        refreshClients()
        return true
    }

    /** Names of the commands registered by this module. */
    public val commands: Set<String> get() = registered.keys.toSet()

    override fun onDisable() {
        registered.values.forEach { remove(it) }
        registered.clear()
    }

    private fun remove(command: Command) {
        command.unregister(commandMap)
        // unregister() leaves the entry behind, so the name would stay taken.
        val map = knownCommands ?: return
        map.filterValues { it == command }.keys.forEach { map.remove(it) }
    }

    /** Without this the client keeps suggesting the previous command list. */
    private fun refreshClients() {
        Bukkit.getOnlinePlayers().forEach { it.updateCommands() }
    }

    /** CraftServer#getCommandMap() is not Bukkit API, but it is public on every version. */
    private val commandMap: CommandMap by lazy {
        val server = Bukkit.getServer()
        server.javaClass.getMethod("getCommandMap").invoke(server) as CommandMap
    }

    /** On Spigot `knownCommands` is a protected field; Paper does expose a public getter. */
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
