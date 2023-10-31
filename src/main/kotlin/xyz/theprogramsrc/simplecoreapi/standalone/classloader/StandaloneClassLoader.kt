package xyz.theprogramsrc.simplecoreapi.standalone.classloader

import java.net.URL
import java.net.URLClassLoader

class StandaloneClassLoader(
    private val standaloneDependencyLoader: StandaloneDependencyLoader,
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
                if (`class`.classLoader is StandaloneClassLoader) {
                    return `class`
                }
            } catch (_: ClassNotFoundException) {}
        }

        throw ClassNotFoundException(name)
    }
}