package xyz.theprogramsrc.simplecoreapi.global.modules.loggingmodule

import xyz.theprogramsrc.simplecoreapi.global.module.Module
import xyz.theprogramsrc.simplecoreapi.global.module.ModuleDescription

class LoggingModule: Module {

    override val description: ModuleDescription = ModuleDescription(
        name = "LoggingModule",
        version = "0.4.1",
        authors = listOf("Im-Fran")
    )

    override fun onEnable() {
    }

    override fun onDisable() {
    }

}