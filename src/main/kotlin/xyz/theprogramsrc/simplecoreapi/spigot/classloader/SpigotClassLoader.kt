package xyz.theprogramsrc.simplecoreapi.spigot.classloader

import java.net.URL
import java.net.URLClassLoader

class SpigotClassLoader(
    private val standaloneDependencyLoader: SpigotDependencyLoader,
    urls: Array<URL>
) : URLClassLoader(urls) {

    override fun loadClass(name: String, resolve: Boolean): Class<*> =
        loadClass0(
            name = name,
            resolve = resolve,
            checkOthers = true
        )

    fun loadClass0(name: String, resolve: Boolean, checkOthers: Boolean): Class<*> {
        try {
            val `class` = super.loadClass(name, resolve)

            if(checkOthers || `class`.classLoader == this) {
                return `class`
            }
        } catch (_: ClassNotFoundException){}

        if (checkOthers) {
            try {
                val `class` = standaloneDependencyLoader.findClassByName(name)
                if (`class`.classLoader is SpigotClassLoader) {
                    return `class`
                }
            } catch (_: ClassNotFoundException) {}
        }

        throw ClassNotFoundException(name)
    }
}