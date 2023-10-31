package xyz.theprogramsrc.simplecoreapi.global.dependencydownloader

import xyz.theprogramsrc.simplecoreapi.global.SimpleCoreAPI
import xyz.theprogramsrc.simplecoreapi.global.dependencydownloader.interfaces.DependencyLoader

/**
 * Representation of the dependency loader
 * @param dependencyClassLoader The [DependencyLoader] to use to load the dependencies
 */
class DependencyLoader(val dependencyClassLoader: DependencyLoader) {

    private val librariesFolder = SimpleCoreAPI.dataFolder("libraries/")

    init {
        // Load all the dependencies from the libraries folder
        val dependencies = resolveDependencies()

        // Now using the dependencyClassLoader, load all the dependencies
        dependencies.forEach {
            SimpleCoreAPI.logger.info("Loading dependency ${it.nameWithoutExtension.replace('-', ':')}...")
            dependencyClassLoader.loadIntoClasspath(it)
        }
    }

    /**
     * Method in charge of resolving the dependencies
     * @return The list of dependencies available to load
     */
    private fun resolveDependencies() = (librariesFolder.listFiles() ?: arrayOf())
        .filter { it.extension == "jar" }
}