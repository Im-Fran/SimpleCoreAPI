package xyz.theprogramsrc.simplecoreapi.standalone

import xyz.theprogramsrc.simplecoreapi.global.SimpleCoreAPI
import xyz.theprogramsrc.simplecoreapi.global.dependencydownloader.Dependency
import xyz.theprogramsrc.simplecoreapi.global.dependencydownloader.DependencyDownloader
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
        val simpleCoreAPI = SimpleCoreAPI(JavaLogger(Logger.getAnonymousLogger()))

        simpleCoreAPI.measureLoad("Loaded dependencies in {time}") {
            DependencyDownloader()
        }.apply {
            addDependency(Dependency("xyz.theprogramsrc", "SimpleCore-FilesModule", ""))
        }

        val entrypoint = simpleCoreAPI.measureLoad("Loaded entrypoint in {time}") {
            EntrypointLoader()
        }

        entrypoint.enable()
        Runtime.getRuntime().addShutdownHook(Thread {
            entrypoint.disable()
        })
    }
}