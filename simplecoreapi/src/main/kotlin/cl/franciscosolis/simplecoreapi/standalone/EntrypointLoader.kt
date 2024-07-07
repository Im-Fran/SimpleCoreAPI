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

package cl.franciscosolis.simplecoreapi.standalone

import cl.franciscosolis.simplecoreapi.global.SimpleCoreAPI
import java.io.File
import java.util.*
import java.util.jar.JarFile
import kotlin.system.exitProcess

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

    init {
        load()
    }

    /**
     * Method in charge of loading the entrypoint
     */
    private fun load() {
        val logger = SimpleCoreAPI.logger

        logger.debug("Loading entrypoint...")
        val entrypointFolder = SimpleCoreAPI.dataFolder(path = "entrypoint/")
        // Check if the folder is empty
        if (entrypointFolder.listFiles()?.isEmpty() == true) {
            // Ask the user to put the entrypoint jar file inside the entrypoint folder and wait until the user does it
            logger.info("Please put the entrypoint jar file inside the ${entrypointFolder.relativeTo(File(".")).path} folder, then press enter to continue.")
            readlnOrNull()
        }

        // Check if the folder is still empty
        if (entrypointFolder.listFiles()?.isEmpty() == true) {
            // If it's still empty, then exit the app
            logger.info("Entrypoint not found. Exiting...")
            exitProcess(0)
        }

        // Check if there's a jar file inside the entrypoint folder
        val entrypointFile = entrypointFolder.listFiles()?.firstOrNull { it.extension == "jar" } ?: return logger.info("No entrypoint found. Skipping...")
        // Check for the file module.properties inside the jar file
        val jarFile = JarFile(entrypointFile)
        val moduleProperties = jarFile.getJarEntry("module.properties")?.let { entry ->
            val properties = Properties()
            jarFile.getInputStream(entry).use { properties.load(it) }
            properties
        } ?: return logger.info("No module.properties found in the entrypoint jar. Skipping...")
        // Check if the module.properties has the entrypoint property to load the entrypoint class
        val entrypointClass = moduleProperties.getProperty("entrypoint") ?: return logger.info("No entrypoint class found in the module.properties. Skipping...")
        logger.debug("Loading entrypoint class $entrypointClass...")
        val entrypoint = Class.forName(entrypointClass).getConstructor().newInstance() as? EntryPoint ?: return logger.info("Entrypoint class $entrypointClass does not implement EntryPoint. Skipping...")
        // Call the onLoad method
        logger.debug("Calling onLoad method...")
        entrypoint.onLoad()
        // Call the onEnable method
        logger.debug("Calling onEnable method...")
        entrypoint.onEnable()
        // Add a shutdown hook to call the onDisable method
        Runtime.getRuntime().addShutdownHook(Thread {
            logger.debug("Calling onDisable method...")
            entrypoint.onDisable()
        })
    }

}
