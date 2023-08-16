package xyz.theprogramsrc.simplecoreapi.spigot

import org.bukkit.plugin.java.JavaPlugin
import xyz.theprogramsrc.simplecoreapi.global.SimpleCoreAPI
import xyz.theprogramsrc.simplecoreapi.global.utils.logger.JavaLogger

class SpigotLoader: JavaPlugin() {

    companion object {
        lateinit var instance: SpigotLoader
            private set
    }

    override fun onLoad() {
        instance = this
        SimpleCoreAPI(JavaLogger(this.logger))
    }

}