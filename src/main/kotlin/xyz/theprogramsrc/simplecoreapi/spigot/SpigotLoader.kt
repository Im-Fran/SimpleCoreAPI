package xyz.theprogramsrc.simplecoreapi.spigot

import org.bukkit.plugin.java.JavaPlugin
import xyz.theprogramsrc.simplecoreapi.global.SimpleCoreAPI

class SpigotLoader: JavaPlugin() {

    companion object {
        lateinit var instance: SpigotLoader
            private set
    }

    override fun onLoad() {
        instance = this
        SimpleCoreAPI(this.logger)
    }

    override fun onEnable() {
        SimpleCoreAPI.instance.moduleManager?.enableModules()
    }

    override fun onDisable() {
        SimpleCoreAPI.instance.moduleManager?.disableModules()
    }

}