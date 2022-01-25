package xyz.theprogramsrc.simplecoreapi.bungee

import net.md_5.bungee.api.plugin.Plugin
import xyz.theprogramsrc.simplecoreapi.global.SimpleCoreAPI

class BungeeLoader: Plugin() {

    companion object {
        lateinit var instance: BungeeLoader
            private set
    }

    override fun onEnable() {
        instance = this
        SimpleCoreAPI(this.logger)
    }

    override fun onDisable() {
        SimpleCoreAPI.instance.moduleManager?.disableModules()
    }

}