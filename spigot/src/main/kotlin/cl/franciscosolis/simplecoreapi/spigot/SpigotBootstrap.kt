package cl.franciscosolis.simplecoreapi.spigot

import cl.franciscosolis.simplecoreapi.Platform
import cl.franciscosolis.simplecoreapi.SimpleCoreAPI
import cl.franciscosolis.simplecoreapi.module.ModuleManager
import org.bukkit.plugin.java.JavaPlugin

/**
 * Spigot plugin that initializes SimpleCoreAPI.
 *
 * It loads no modules: each one is instantiated when the first plugin asks for it.
 */
public class SpigotBootstrap : JavaPlugin() {

    override fun onEnable() {
        instance = this
        SimpleCoreAPI.init(Platform.SPIGOT, logger, dataFolder)
    }

    override fun onDisable() {
        ModuleManager.disableAll()
        SimpleCoreAPI.reset()
    }

    public companion object {

        /** The plugin instance, available from the moment the server enables it. */
        public lateinit var instance: SpigotBootstrap
            private set
    }
}
