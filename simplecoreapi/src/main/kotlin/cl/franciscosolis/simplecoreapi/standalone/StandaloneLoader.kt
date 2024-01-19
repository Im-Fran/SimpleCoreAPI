package cl.franciscosolis.simplecoreapi.standalone

import cl.franciscosolis.simplecoreapi.global.SimpleCoreAPI
import cl.franciscosolis.simplecoreapi.global.utils.measureLoad

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
        SimpleCoreAPI()

        measureLoad("Loaded entrypoint in {time}") {
            EntrypointLoader()
        }
    }
}