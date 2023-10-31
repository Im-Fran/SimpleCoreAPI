package xyz.theprogramsrc.simplecoreapi.spigot.classloader

import xyz.theprogramsrc.simplecoreapi.global.dependencydownloader.interfaces.DependencyLoader
import java.io.File

class SpigotDependencyLoader: DependencyLoader {

    companion object {

    }

    override fun loadIntoClasspath(file: File) {
        TODO("Not yet implemented")
    }

    override fun findClassByName(name: String): Class<*> {
        TODO("Not yet implemented")
    }
}