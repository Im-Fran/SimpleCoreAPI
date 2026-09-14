package cl.franciscosolis.simplecoreapi.paper.modules.command

import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.plugin.Plugin

/**
 * Bridge between Paper's lifecycle and the lazily loaded [CommandModule].
 *
 * Paper only accepts lifecycle handlers during the owning plugin's `onEnable`: by the time
 * another plugin asks for [CommandModule] that window is already closed. So the handler is
 * installed on SimpleCoreAPI startup -it costs nothing and loads no module- and this object
 * only collects the builders that arrive later.
 */
internal object BrigadierCommands {

    private val builders = mutableListOf<(Commands) -> Unit>()

    /**
     * Installs the `COMMANDS` event handler. Called from `PaperBootstrap.onEnable`.
     *
     * The event is fired again on every datapack reload, which is why [builders] is kept
     * around and replayed in full each time.
     */
    fun install(plugin: Plugin) {
        plugin.lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
            val registrar = event.registrar()
            synchronized(this) { builders.toList() }.forEach { it(registrar) }
        }
    }

    @Synchronized
    fun add(builder: (Commands) -> Unit) {
        builders += builder
    }

    @Synchronized
    fun clear() {
        builders.clear()
    }
}
