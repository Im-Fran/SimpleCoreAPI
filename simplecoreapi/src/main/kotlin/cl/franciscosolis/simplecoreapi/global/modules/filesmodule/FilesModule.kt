package cl.franciscosolis.simplecoreapi.global.modules.filesmodule

import cl.franciscosolis.simplecoreapi.global.module.Module
import cl.franciscosolis.simplecoreapi.global.module.ModuleDescription

class FilesModule: Module {

    override val description: ModuleDescription =
        ModuleDescription(
            name = "FilesModule",
            version = "0.4.0",
            authors = listOf("Im-Fran")
        )

    override fun onEnable() {

    }

    override fun onDisable() {

    }

}