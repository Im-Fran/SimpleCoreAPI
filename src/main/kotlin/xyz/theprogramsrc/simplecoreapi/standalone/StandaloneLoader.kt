package xyz.theprogramsrc.simplecoreapi.standalone

import xyz.theprogramsrc.simplecoreapi.global.SimpleCoreAPI
import xyz.theprogramsrc.simplecoreapi.standalone.classloader.StandaloneDependencyLoader

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
        val simpleCoreAPI = SimpleCoreAPI(
            dependencyClassLoader = StandaloneDependencyLoader(),
        )

        simpleCoreAPI.measureLoad("Loaded entrypoint in {time}") {
            EntrypointLoader()
        }
    }
}