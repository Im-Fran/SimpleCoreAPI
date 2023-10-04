package xyz.theprogramsrc.simplecoreapi.global.models

/**
 * This interface represents a module.
 * It is used to load modules on demand.
 */
interface Module {

    /**
     * This method is called when the module is loaded.
     */
    fun onEnable()

    /**
     * This method is called when the module is unloaded.
     */
    fun onDisable()

    companion object {
        val loadedModules: MutableMap<String, Module> = mutableMapOf()

        /**
         * This method is used to load a module.
         *
         * Example usage:
         * ```kotlin
         * val myModule = Module.require<MyModule>()
         * ```
         *
         *
         * @param T The module class
         * @return The module instance
         */
        inline fun <reified T : Module> require(): T {
            val moduleName = T::class.java.canonicalName

            return loadedModules.computeIfAbsent(moduleName) {
                val moduleInstance = T::class.java.getConstructor().newInstance()
                moduleInstance.onEnable()
                loadedModules[moduleName] = moduleInstance
                Runtime.getRuntime().addShutdownHook(Thread {
                    moduleInstance.onDisable()
                })
                moduleInstance
            } as T
        }
    }
}
