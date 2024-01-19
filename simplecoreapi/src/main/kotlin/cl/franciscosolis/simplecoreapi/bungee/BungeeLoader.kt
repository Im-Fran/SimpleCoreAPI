package cl.franciscosolis.simplecoreapi.bungee

import net.md_5.bungee.api.plugin.Plugin
import cl.franciscosolis.simplecoreapi.global.SimpleCoreAPI

class BungeeLoader: Plugin() {

    companion object {
        lateinit var instance: cl.franciscosolis.simplecoreapi.bungee.BungeeLoader
            private set
    }

    override fun onLoad() {
        cl.franciscosolis.simplecoreapi.bungee.BungeeLoader.Companion.instance = this
        SimpleCoreAPI()
    }

}