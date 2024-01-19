package cl.franciscosolis.simplecoreapi.global.modules.translationsmodule

import cl.franciscosolis.simplecoreapi.global.module.Module
import cl.franciscosolis.simplecoreapi.global.module.ModuleDescription
import cl.franciscosolis.simplecoreapi.global.modules.translationsmodule.managers.TranslationManager
import cl.franciscosolis.simplecoreapi.global.utils.measureLoad

class TranslationsModule: Module {
    override val description: ModuleDescription = ModuleDescription(
        name = "TranslationsModule",
        version = "0.4.0",
        authors = listOf("Im-Fran")
    )

    override fun onEnable() {
        measureLoad("'TranslationManager' loaded in {time}"){
            TranslationManager()
        }
    }

    override fun onDisable() {
    }
}