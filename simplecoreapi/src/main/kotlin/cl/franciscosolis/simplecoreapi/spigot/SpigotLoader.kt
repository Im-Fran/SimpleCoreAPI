package cl.franciscosolis.simplecoreapi.spigot

import org.bukkit.plugin.java.JavaPlugin
import cl.franciscosolis.simplecoreapi.global.SimpleCoreAPI
import cl.franciscosolis.simplecoreapi.spigot.events.AsyncConfigurationReloadEvent
import cl.franciscosolis.simplecoreapi.spigot.events.ConfigurationReloadEvent
import cl.franciscosolis.simplecoreapi.spigot.modules.tasksmodule.SpigotTasksModule

/**
 * Representation of the Spigot plugin loader.
 */
class SpigotLoader: JavaPlugin() {

    companion object {

        /**
         * Instance of the SpigotLoader, useful for accessing some spigot-specific methods.
         */
        lateinit var instance: SpigotLoader
            private set
    }

    override fun onLoad() {
        instance = this

        SimpleCoreAPI()
    }

    /**
     * This function should only be called if you need to reload settings.
     * This will fire the [ConfigurationReloadEvent] and [AsyncConfigurationReloadEvent] events.
     */
    fun fireSettingsReload() {
        SpigotTasksModule.instance.runTask {
            server.pluginManager.callEvent(ConfigurationReloadEvent())
        }

        SpigotTasksModule.instance.runTaskAsynchronously {
            server.pluginManager.callEvent(AsyncConfigurationReloadEvent())
        }
    }

}