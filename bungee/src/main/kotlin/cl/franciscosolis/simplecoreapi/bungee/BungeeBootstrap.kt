package cl.franciscosolis.simplecoreapi.bungee

import cl.franciscosolis.simplecoreapi.Platform
import cl.franciscosolis.simplecoreapi.SimpleCoreAPI
import cl.franciscosolis.simplecoreapi.module.ModuleManager
import net.md_5.bungee.api.plugin.Plugin

/**
 * BungeeCord plugin that initializes SimpleCoreAPI.
 *
 * It loads no modules: each one is instantiated when the first plugin asks for it.
 */
public class BungeeBootstrap : Plugin() {

    override fun onEnable() {
        instance = this
        SimpleCoreAPI.init(Platform.BUNGEE, logger, dataFolder)
    }

    override fun onDisable() {
        ModuleManager.disableAll()
        SimpleCoreAPI.reset()
    }

    public companion object {

        /** The plugin instance, available from the moment BungeeCord enables it. */
        public lateinit var instance: BungeeBootstrap
            private set
    }
}
