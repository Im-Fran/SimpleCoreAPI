package xyz.theprogramsrc.simplecoreapi.spigot

import org.bukkit.plugin.java.JavaPlugin
import xyz.theprogramsrc.simplecoreapi.global.SimpleCoreAPI

/**
 * Representation of the Spigot plugin loader.
 */
class SpigotLoader: JavaPlugin() {

    companion object {

        /**
         * Instance of the SpigotLoader, useful for accessing some spigot specific methods.
         */
        lateinit var instance: SpigotLoader
            private set
    }

    override fun onLoad() {
        instance = this

        SimpleCoreAPI()
    }

}