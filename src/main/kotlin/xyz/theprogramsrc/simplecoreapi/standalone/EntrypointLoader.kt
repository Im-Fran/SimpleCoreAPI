package xyz.theprogramsrc.simplecoreapi.standalone

import xyz.theprogramsrc.simplecoreapi.global.SimpleCoreAPI
import java.io.File
import java.util.Properties
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
//        val loader = ModuleClassLoader(entrypointFile)
//        val entrypoint = loader.loadClass(entrypointClass).getDeclaredConstructor().newInstance() as? EntryPoint ?: return logger.info("Entrypoint class does not implement EntryPoint interface. Skipping...")
//        // Call the onLoad method
//        entrypoint.onLoad()
//        // Call the onEnable method
//        entrypoint.onEnable()
//        // Register the onDisable method to be called when the app is disabled
//        Runtime.getRuntime().addShutdownHook(Thread { entrypoint.onDisable() })
    }

}
