/*
 * SimpleCoreAPI - Kotlin Project Library
 * Copyright (C) 2024 Francisco Solís
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package cl.franciscosolis.simplecoreapi.global.module

import cl.franciscosolis.simplecoreapi.global.utils.measureLoad
import java.util.*

/**
 * This interface represents a module.
 * It is used to load modules on demand.
 */
interface Module {

    /**
     * The module description.
     * This is used to get information about the module.
     * @see ModuleDescription
     */
    val description: ModuleDescription

    /**
     * This method is called when the module is loaded.
     */
    fun onEnable()

    /**
     * This method is called when the module is unloaded.
     */
    fun onDisable()

    companion object {
        /**
         * **INTERNAL USE ONLY**
         * This is used to store the loaded modules.
         * @see Module
         * @see requireModule
         */
        val loadedModules: MutableMap<String, Module> = mutableMapOf()
    }
}

/**
 * This method is used to load a module.
 *
 * Example usage:
 * ```kotlin
 * val myModule = requireModule<MyModule>()
 * myModule.doSomething()
 * ```
 *
 * @param T The module class
 * @return The module instance
 */
inline fun <reified T : Module> requireModule(): T {
    val id = UUID.nameUUIDFromBytes(("${T::class.java.name}${T::class.java.classLoader}${T::class.java.`package`.name}").toByteArray()).toString()
    if(Module.loadedModules.containsKey(id)){
        return Module.loadedModules[id] as T
    }


    val moduleInstance = T::class.java.getConstructor().newInstance()
    Module.loadedModules[id] = moduleInstance
    measureLoad("Module ${moduleInstance.description.name} enabled in {time}") {
        moduleInstance.onEnable()
    }

    Runtime.getRuntime().addShutdownHook(Thread {
        measureLoad("Module ${moduleInstance.description.name} disabled in {time}") {
            moduleInstance.onDisable()
        }
    })

    return moduleInstance
}

/**
 * This method is used to validate if a module has been loaded or not.
 *
 * Example usage:
 * ```kotlin
 * if(!isModuleLoaded<MyModule>()){
 *    // Do something
 * }
 * ```
 *
 * @param T The module class
 * @return True if the module is loaded, false otherwise
 */
inline fun <reified T : Module> isModuleLoaded(): Boolean {
    val name = UUID.nameUUIDFromBytes(("${T::class.java.name}${T::class.java.classLoader}${T::class.java.`package`.name}").toByteArray()).toString()
    return Module.loadedModules.containsKey(name)
}
