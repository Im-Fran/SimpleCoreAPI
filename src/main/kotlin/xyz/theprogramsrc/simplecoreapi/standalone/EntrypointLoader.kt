package xyz.theprogramsrc.simplecoreapi.standalone

import java.util.Properties

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR)
@Retention(AnnotationRetention.RUNTIME)
annotation class EntryPoint

class EntrypointLoader {

    init {
        // First get the resource 'module.properties' located at the root of the jar file
        val moduleProperties = EntrypointLoader::class.java.getResourceAsStream("/module.properties")
        if(moduleProperties != null) {
            // Now read the 'entrypoint' property
            val entrypoint = (Properties().let {
                it.load(moduleProperties)
                it.getProperty("entrypoint")
            } ?: "").replace("\"", "")

            assert(entrypoint.isNotBlank()) { "Entrypoint cannot be blank!" }

            // Now load the class
            val clazz = this::class.java.classLoader.loadClass(entrypoint)

            // Now check if the class itself is an entrypoint, if it is, initialize it, if not check for the first method that is an entrypoint
            if(clazz.isAnnotationPresent(EntryPoint::class.java)){
                clazz.getConstructor().newInstance()
            } else {
                clazz.methods.forEach { method ->
                    if(method.isAnnotationPresent(EntryPoint::class.java)){
                        method.invoke(null)
                        return@forEach
                    }
                }
            }
        }
    }
}
