package xyz.theprogramsrc.simplecoreapi.spigot.modules.uismodule

import xyz.theprogramsrc.simplecoreapi.global.module.Module
import xyz.theprogramsrc.simplecoreapi.global.module.ModuleDescription

class UIsModule : Module {

    override val description: ModuleDescription = ModuleDescription(
        name = "UIsModule",
        version = "1.0.0",
        authors = listOf("Im-Fran")
    )

    override fun onEnable() {}

    override fun onDisable() {}
}