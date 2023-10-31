package xyz.theprogramsrc.simplecoreapi.global.dependencydownloader.interfaces

import java.io.File

interface DependencyLoader {

    fun loadIntoClasspath(file: File)

    fun findClassByName(name: String): Class<*>
}