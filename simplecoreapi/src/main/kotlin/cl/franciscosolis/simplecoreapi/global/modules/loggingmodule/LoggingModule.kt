package cl.franciscosolis.simplecoreapi.global.modules.loggingmodule

import cl.franciscosolis.simplecoreapi.global.module.Module
import cl.franciscosolis.simplecoreapi.global.module.ModuleDescription

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