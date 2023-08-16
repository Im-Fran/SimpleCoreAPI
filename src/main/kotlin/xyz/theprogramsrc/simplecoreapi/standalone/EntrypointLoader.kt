package xyz.theprogramsrc.simplecoreapi.standalone

import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.util.zip.ZipInputStream

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR)
@Retention(AnnotationRetention.RUNTIME)
annotation class EntryPoint

class EntrypointLoader {

    init {
        val classLoader = URLClassLoader(
            System.getProperty("java.class.path").split(File.pathSeparator).map { File(it).toURI().toURL() }.toTypedArray(),
            this::class.java.classLoader
        )

        val files = mutableListOf<String>().apply {
            try {
                val src = EntrypointLoader::class.java.protectionDomain.codeSource
                val jar = src.location
                val zipInputStream = ZipInputStream(jar.openStream())
                while (true) {
                    val entry = zipInputStream.nextEntry ?: break
                    if (entry.name.endsWith(".class")) {
                        add(entry.name.replace("/", ".").replace(".class", ""))
                    }
                }
            } catch (_: Exception) {
            }
        }

        files.forEach { className ->
            try {
                val clazz = Class.forName(className, false, classLoader)
                val entryPointAnnotation = clazz.getAnnotation(EntryPoint::class.java)

                if (entryPointAnnotation != null) {
                    when {
                        clazz.isAnnotationPresent(EntryPoint::class.java) -> {
                            // If the @EntryPoint annotation is present on a class, call its constructor.
                            clazz.getDeclaredConstructor().newInstance()
                            return@forEach
                        }

                        clazz.declaredMethods.any { it.isAnnotationPresent(EntryPoint::class.java) } -> {
                            // If the @EntryPoint annotation is present on a function, find and call that function.
                            val entryPointMethods = clazz.declaredMethods.filter { it.isAnnotationPresent(EntryPoint::class.java) }
                            for (method in entryPointMethods) {
                                method.invoke(clazz.getDeclaredConstructor().newInstance())
                                return@forEach
                            }
                        }
                    }
                }
            } catch (_: Exception) {
            }
        }
    }
}
