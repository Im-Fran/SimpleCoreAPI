package cl.franciscosolis.simplecoreapi.paper

import cl.franciscosolis.simplecoreapi.Platform
import cl.franciscosolis.simplecoreapi.SimpleCoreAPI
import cl.franciscosolis.simplecoreapi.module.ModuleManager
import cl.franciscosolis.simplecoreapi.paper.modules.command.BrigadierCommands
import org.bukkit.plugin.java.JavaPlugin

/**
 * Paper plugin that initializes SimpleCoreAPI.
 *
 * It loads no modules: each one is instantiated when the first plugin asks for it.
 */
public class PaperBootstrap : JavaPlugin() {

    override fun onEnable() {
        instance = this
        SimpleCoreAPI.init(Platform.PAPER, logger, dataFolder)
        // Paper requires lifecycle handlers to be registered right now; doing it later fails.
        // This instantiates no module, it only leaves the hook in place.
        BrigadierCommands.install(this)
    }

    override fun onDisable() {
        ModuleManager.disableAll()
        SimpleCoreAPI.reset()
    }

    public companion object {

        /** The plugin instance, available from the moment the server enables it. */
        public lateinit var instance: PaperBootstrap
            private set
    }
}
