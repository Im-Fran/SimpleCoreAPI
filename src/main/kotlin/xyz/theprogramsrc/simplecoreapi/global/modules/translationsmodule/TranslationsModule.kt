package xyz.theprogramsrc.simplecoreapi.global.modules.translationsmodule

import xyz.theprogramsrc.simplecoreapi.global.module.Module
import xyz.theprogramsrc.simplecoreapi.global.module.ModuleDescription
import xyz.theprogramsrc.simplecoreapi.global.modules.translationsmodule.managers.TranslationManager
import xyz.theprogramsrc.simplecoreapi.global.utils.measureLoad

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