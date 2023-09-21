package xyz.theprogramsrc.simplecoreapi.standalone

import java.util.Properties
import java.util.zip.ZipInputStream

/**
 * Interface that will be used to load the entry point of the app.
 */
interface EntryPoint {

    /**
     * Called when the app is loaded
     */
    fun onLoad()

    /**
     * Called when the app is enabled
     */
    fun onEnable()

    /**
     * Called when the app is disabled
     */
    fun onDisable()
}

/**
 * Class that manages the entry point of the app. It will be in charge of running the onLoad, onEnable and onDisable methods of the class importing [EntryPoint].
 */
class EntrypointLoader {
    companion object {
        private var entryPoint: EntryPoint? = null

        /**
         * Manually register the entrypoint.
         * Currently, this is used for testing purposes, but if you have issues with the entrypoint not being loaded, you can use this method to register it manually.
         *
         * @param clazz The entrypoint class. It must implement [EntryPoint]
         */
        fun registerEntrypoint(clazz: Class<out EntryPoint>) {
            entryPoint = clazz.getConstructor().newInstance() as EntryPoint
        }
    }
    private var enabled: Boolean = false

    init {
        if(entryPoint == null) {
            // First get the resource 'module.properties' located at the root of the jar file
            val moduleProperties = EntrypointLoader::class.java.getResourceAsStream("/module.properties")
            if(moduleProperties != null) {
                // Now read the 'entrypoint' property
                val entrypoint = (Properties().let {
                    it.load(moduleProperties)
                    it.getProperty("entrypoint")
                } ?: "").replace("\"", "")

                assert(entrypoint.isNotBlank()) { "Entrypoint cannot be blank!" }

                // Now load the class
                val clazz = this::class.java.classLoader.loadClass(entrypoint)

                // Now check if the class itself is an entrypoint, if it is, initialize it, if not check for the first method that is an entrypoint
                if(clazz.isAssignableFrom(EntryPoint::class.java)){
                    entryPoint = clazz.getConstructor().newInstance() as EntryPoint
                }
            }
        }

        entryPoint?.onLoad()
    }

    fun enable() {
        assert(!enabled) { "App already enabled! Please avoid calling this method more than once." }
        entryPoint?.onEnable()
        enabled = true
    }

    fun disable() {
        assert(enabled) { "App already disabled! Please avoid calling this method more than once." }
        entryPoint?.onDisable()
        enabled = false
    }
}
