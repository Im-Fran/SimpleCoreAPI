package xyz.theprogramsrc.simplecoreapi.spigot

import org.bukkit.plugin.java.JavaPlugin
import xyz.theprogramsrc.simplecoreapi.global.SimpleCoreAPI
import xyz.theprogramsrc.simplecoreapi.spigot.events.AsyncConfigurationReloadEvent
import xyz.theprogramsrc.simplecoreapi.spigot.events.ConfigurationReloadEvent
import xyz.theprogramsrc.simplecoreapi.spigot.modules.tasksmodule.SpigotTasksModule

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