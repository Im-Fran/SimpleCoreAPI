package xyz.theprogramsrc.simplecoreapi.global

import xyz.theprogramsrc.simplecoreapi.global.module.ModuleManager
import java.util.logging.Logger

class SimpleCoreAPI(logger: Logger) {

    companion object {
        lateinit var instance: SimpleCoreAPI
    }

    val moduleManager: ModuleManager

    init {
        instance = this
        moduleManager = ModuleManager.init(logger)
    }

}