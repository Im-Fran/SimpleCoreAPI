package xyz.theprogramsrc.simplecoreapi.global.models

import xyz.theprogramsrc.simplecoreapi.global.SimpleCoreAPI
import xyz.theprogramsrc.simplecoreapi.global.utils.ILogger
import java.util.UUID

/**
 * This interface represents a module.
 * It is used to load modules on demand.
 */
open class Module {

    /**
     * The SimpleCoreAPI instance.
     * This is used to access the API instance and its methods.
     * @see SimpleCoreAPI
     */
    protected val simplecoreapi: SimpleCoreAPI = SimpleCoreAPI.instance

    /**
     * The ILogger instance.
     * This is used to log messages to the console.
     * @see ILogger
     */
    protected val logger: ILogger = simplecoreapi.logger

    /**
     * This method is called when the module is loaded.
     */
    open fun onEnable() {}

    /**
     * This method is called when the module is unloaded.
     */
    open fun onDisable() {}

    companion object {
        val loadedModules: MutableMap<String, Module> = mutableMapOf()
    }
}

/**
 * This method is used to load a module.
 *
 * Example usage:
 * ```kotlin
 * val myModule = requireModule<MyModule>()
 * ```
 *
 *
 * @param T The module class
 * @return The module instance
 */
inline fun <reified T : Module> requireModule(): T {
    val moduleName = UUID.nameUUIDFromBytes("${T::class.java.name}${T::class.java.classLoader}${T::class.java.`package`.name}".toByteArray()).toString()
    println("Name for ${T::class.java.name}: $moduleName")

    return Module.loadedModules.computeIfAbsent(moduleName) {
        val moduleInstance = T::class.java.getConstructor().newInstance()
        moduleInstance.onEnable()
        Module.loadedModules[moduleName] = moduleInstance
        Runtime.getRuntime().addShutdownHook(Thread {
            moduleInstance.onDisable()
        })
        moduleInstance
    } as T
}
