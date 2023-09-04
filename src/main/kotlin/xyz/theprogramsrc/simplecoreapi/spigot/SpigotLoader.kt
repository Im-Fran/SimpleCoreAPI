package xyz.theprogramsrc.simplecoreapi.spigot

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import xyz.theprogramsrc.simplecoreapi.global.SimpleCoreAPI
import xyz.theprogramsrc.simplecoreapi.global.utils.logger.JavaLogger
import java.io.File

class SpigotLoader: JavaPlugin() {

    companion object {
        lateinit var instance: SpigotLoader
            private set

        /**
         * **INTERNAL USE ONLY**
         * Loads the given file using bukkit plugin manager.
         */
        fun loadFile(file: File) = Bukkit.getPluginManager().loadPlugin(file)?.apply {
            onLoad()
            Bukkit.getPluginManager().enablePlugin(this@apply)
        }
    }

    override fun onLoad() {
        instance = this
        SimpleCoreAPI(JavaLogger(this.logger))
    }

}