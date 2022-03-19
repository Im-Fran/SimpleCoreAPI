package xyz.theprogramsrc.simplecoreapi.bungee

import net.md_5.bungee.api.plugin.Plugin
import xyz.theprogramsrc.simplecoreapi.global.SimpleCoreAPI

class BungeeLoader: Plugin() {

    companion object {
        lateinit var instance: BungeeLoader
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