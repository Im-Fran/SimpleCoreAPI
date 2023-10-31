package xyz.theprogramsrc.simplecoreapi.standalone.classloader

import xyz.theprogramsrc.simplecoreapi.global.dependencydownloader.interfaces.DependencyLoader
import java.io.File

class StandaloneDependencyLoader : DependencyLoader {

    private val classLoaders = mutableListOf<StandaloneClassLoader>()

    override fun loadIntoClasspath(file: File) {
        val classLoader = StandaloneClassLoader(this, arrayOf(file.toURI().toURL()))
        classLoaders.add(classLoader)
    }

    override fun findClassByName(name: String): Class<*> = classLoaders.firstNotNullOfOrNull {
        try {
            it.loadClass0(
                name = name,
                resolve = true,
                checkOthers = false
            )
        } catch (_: ClassNotFoundException) {
            null
        }
    } ?: throw ClassNotFoundException(name)
}