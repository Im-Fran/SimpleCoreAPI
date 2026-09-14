package cl.franciscosolis.simplecoreapi.bungee.modules.command

import cl.franciscosolis.simplecoreapi.bungee.BungeeBootstrap
import cl.franciscosolis.simplecoreapi.module.Module
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.api.plugin.TabExecutor

/**
 * Command registration on BungeeCord.
 *
 * ```kotlin
 * val commands = requireModule<CommandModule>()
 * commands.registerCommand("lobby", permission = "network.lobby") { sender, args ->
 *     sender.sendMessage(TextComponent("Sending you to the lobby..."))
 * }
 * ```
 *
 * Commands are registered under the SimpleCoreAPI plugin and are removed automatically when
 * the proxy shuts down.
 */
public class CommandModule : Module {

    private val registered = LinkedHashMap<String, Command>()

    /**
     * Registers a command.
     *
     * @param permission permission required to run it, or `null` to allow anyone.
     *   BungeeCord checks it before calling [executor].
     * @param tabCompleter completion suggestions, or `null` to offer none.
     * @return the registered command, usable with [unregisterCommand].
     */
    public fun registerCommand(
        name: String,
        aliases: List<String> = emptyList(),
        permission: String? = null,
        tabCompleter: ((sender: CommandSender, args: Array<String>) -> List<String>)? = null,
        executor: (sender: CommandSender, args: Array<String>) -> Unit,
    ): Command {
        unregisterCommand(name)

        val command = object : Command(name, permission, *aliases.toTypedArray()), TabExecutor {
            override fun execute(sender: CommandSender, args: Array<String>) {
                executor(sender, args)
            }

            override fun onTabComplete(sender: CommandSender, args: Array<String>): Iterable<String> =
                tabCompleter?.invoke(sender, args) ?: emptyList()
        }

        ProxyServer.getInstance().pluginManager.registerCommand(BungeeBootstrap.instance, command)
        registered[name.lowercase()] = command
        return command
    }

    /** Removes a command registered by this module. @return `true` if it existed. */
    public fun unregisterCommand(name: String): Boolean {
        val command = registered.remove(name.lowercase()) ?: return false
        ProxyServer.getInstance().pluginManager.unregisterCommand(command)
        return true
    }

    /** Names of the commands registered by this module. */
    public val commands: Set<String> get() = registered.keys.toSet()

    override fun onDisable() {
        registered.values.forEach { ProxyServer.getInstance().pluginManager.unregisterCommand(it) }
        registered.clear()
    }
}
