package cl.franciscosolis.simplecoreapi.paper

import cl.franciscosolis.simplecoreapi.SimpleCoreAPI
import cl.franciscosolis.simplecoreapi.paper.events.AsyncConfigurationReloadEvent
import cl.franciscosolis.simplecoreapi.paper.events.ConfigurationReloadEvent
import cl.franciscosolis.simplecoreapi.paper.extensions.callEvent
import org.bukkit.plugin.java.JavaPlugin

class PaperLoader: JavaPlugin() {

    companion object {

        /**
         * Instance of the [PaperLoader], useful for accessing some paper-specific methods.
         */
        lateinit var instance: PaperLoader
            private set
    }

    override fun onLoad() {
        instance = this
        SimpleCoreAPI(logger = this.logger)
    }

    /**
     * This function should only be called if you need to reload settings.
     * This will fire the [ConfigurationReloadEvent] and [AsyncConfigurationReloadEvent] events.
     */
    fun fireSettingsReload() {
        callEvent(ConfigurationReloadEvent())
        callEvent(AsyncConfigurationReloadEvent())
    }
}