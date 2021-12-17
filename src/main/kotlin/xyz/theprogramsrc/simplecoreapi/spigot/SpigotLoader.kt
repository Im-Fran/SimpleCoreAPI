package xyz.theprogramsrc.simplecoreapi.spigot

import org.bukkit.plugin.java.JavaPlugin
import xyz.theprogramsrc.simplecoreapi.global.SimpleCoreAPI

class SpigotLoader: JavaPlugin() {

    override fun onEnable() {
        SimpleCoreAPI(this.logger)
    }

    override fun onDisable() {
        SimpleCoreAPI.instance.moduleManager?.disableModules()
    }

}