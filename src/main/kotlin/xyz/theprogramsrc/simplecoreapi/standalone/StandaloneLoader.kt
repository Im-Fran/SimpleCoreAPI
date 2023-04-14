package xyz.theprogramsrc.simplecoreapi.standalone

import xyz.theprogramsrc.simplecoreapi.global.SimpleCoreAPI
import xyz.theprogramsrc.simplecoreapi.global.utils.logger.JavaLogger
import java.util.logging.Logger

fun main() {
    StandaloneLoader()
}

class StandaloneLoader {

    companion object {
        lateinit var instance: StandaloneLoader
            private set

        var isRunning = false
            private set
    }

    init {
        instance = this
        isRunning = true
        SimpleCoreAPI(JavaLogger(Logger.getAnonymousLogger()))
        SimpleCoreAPI.instance.moduleManager?.enableModules()

        Runtime.getRuntime().addShutdownHook(Thread {
            SimpleCoreAPI.instance.moduleManager?.disableModules()
        }.apply {
            name = "SimpleCoreAPI Shutdown Hook"
        })
    }
}