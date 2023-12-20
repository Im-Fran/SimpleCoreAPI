package xyz.theprogramsrc.simplecoreapi.global.modules.networkingmodule

import xyz.theprogramsrc.simplecoreapi.global.module.Module
import xyz.theprogramsrc.simplecoreapi.global.module.ModuleDescription

class NetworkingModule: Module {
    override val description: ModuleDescription = ModuleDescription(
        name = "NetworkingModule",
        version = "0.1.0",
        authors = listOf("Im-Fran")
    )

    override fun onEnable() {
    }

    override fun onDisable() {
    }
}