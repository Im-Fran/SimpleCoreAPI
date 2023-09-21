package xyz.theprogramsrc.simplecoreapi.standalone

import xyz.theprogramsrc.simplecoreapi.global.SimpleCoreAPI
import xyz.theprogramsrc.simplecoreapi.global.modules.ModuleManager
import java.io.File
import java.net.URLClassLoader
import java.util.Properties
import java.util.Stack
import java.util.function.Consumer
import java.util.jar.JarFile
import java.util.jar.JarInputStream

data class ModuleDescription(
    val name: String,
    val version: String,
    val author: String,
    val main: String,
    val moduleId: String,
    val dependencies: List<String> = emptyList(),
)

interface Module {
    fun onEnable()

    fun onDisable()
}

class ModuleLoader {

    private val files = mutableMapOf<File, ModuleDescription>()
    private val modulesFolder = SimpleCoreAPI.dataFolder("modules/").apply {
        if(!exists())
            mkdirs()
    }

    init {
        // Check for any requirements in the simplecoreapi.properties entry
        val properties = Properties()
        // Check if the file exists (in the jar)
        val propertiesFile = this.javaClass.classLoader.getResourceAsStream("simplecoreapi.properties")
        if(propertiesFile != null) {
            properties.load(propertiesFile)
        }

        if(properties.getProperty("require") != null) {
            // Load all the required modules
            properties.getProperty("require").split(",").forEach { repositoryId ->
                ModuleManager.downloadModule(repositoryId)
            }
        }

        // Load all files from the modules folder
        fun loadDescriptionFiles() {
            (modulesFolder.listFiles() ?: emptyArray()).filter { it.isFile && it.extension == "jar" }.forEach {
                // Read the file and check if there's a module.properties file
                val jarFile = JarFile(it.absolutePath)
                val moduleProperties = jarFile.getJarEntry("module.properties")?.let { entry ->
                    jarFile.getInputStream(entry).use { stream ->
                        stream.bufferedReader().use { reader ->
                            val props = Properties()
                            props.load(reader)
                            props
                        }
                    }
                } ?: return@forEach
                if(files.containsKey(it)) return@forEach // Already loaded this file

                // Check if the module.properties file contains the required fields
                for (requiredField in arrayOf("name", "version", "author", "main", "module-id")) {
                    assert(moduleProperties.containsKey(requiredField)) { "Module ${it.nameWithoutExtension} is missing the required field '$requiredField'." }
                }
                // Add the file to the files map
                files[it] = ModuleDescription(
                    name = moduleProperties.getProperty("name").let { name ->
                        if(name.startsWith('"') && name.endsWith('"')) name.substring(1, name.length - 1) else name
                    },
                    version = moduleProperties.getProperty("version").let { version ->
                        if(version.startsWith('"') && version.endsWith('"')) version.substring(1, version.length - 1) else version
                    },
                    author = moduleProperties.getProperty("author").let { author ->
                        if(author.startsWith('"') && author.endsWith('"')) author.substring(1, author.length - 1) else author
                    },
                    main = moduleProperties.getProperty("main").let { main ->
                        if(main.startsWith('"') && main.endsWith('"')) main.substring(1, main.length - 1) else main
                    },
                    dependencies = moduleProperties.getProperty("dependencies")?.let { dependencies ->
                        if(dependencies.startsWith('"') && dependencies.endsWith('"')) dependencies.substring(1, dependencies.length - 1) else dependencies
                    }?.split(",") ?: emptyList(),
                    moduleId = moduleProperties.getProperty("module-id").let { moduleId ->
                        if(moduleId.startsWith('"') && moduleId.endsWith('"')) moduleId.substring(1, moduleId.length - 1) else moduleId
                    }
                )
            }
        }

        // Load the description files
        loadDescriptionFiles()

        // Now make sure all files have their dependencies
        files.values.forEach { description ->
            description.dependencies.forEach { dependencyId ->
                if(!files.any { it.value.moduleId ==  dependencyId }) {
                    ModuleManager.downloadModule(dependencyId)
                    loadDescriptionFiles()
                }
            }
        }

        // Now we need to order the modules
        val visited = mutableSetOf<File>()
        val stack = Stack<File>()

        fun topologicalSort(file: File) {
            if (visited.contains(file)) return
            visited.add(file)

            val description = files[file] ?: return
            description.dependencies.forEach { dependency ->
                val dependencyFile = files.keys.firstOrNull { it.nameWithoutExtension == dependency }
                if (dependencyFile != null) {
                    topologicalSort(dependencyFile)
                }
            }

            stack.push(file)
        }

        files.keys.forEach { topologicalSort(it) }

        val loadedModules = mutableListOf<Module>()
        val loader = URLClassLoader(stack.map { it.toURI().toURL() }.toTypedArray(), this.javaClass.classLoader)

        // Finally we need to load the modules by loading the jar files into the classpath and then one by one calling their main class #onEnable method
        for (file in stack) {
            val description = files[file] ?: continue
            // Load the main class
            JarInputStream(file.inputStream()).use {
                try {
                    val mainClass = loader.loadClass(description.main)
                    if(mainClass.interfaces.contains(Module::class.java)) {
                        // Load module
                        val instance = mainClass.getDeclaredConstructor().newInstance() as Module
                        instance.onEnable()
                        loadedModules.add(instance)
                    }
                }catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        // Finally we need to add a shutdown hook to disable all modules
        Runtime.getRuntime().addShutdownHook(Thread {
            loadedModules.forEach { it.onDisable() }
        }.apply {
            name = "SimpleCoreAPI Shutdown Hook"
        })

        // And we're done!
        println("Loaded ${loadedModules.size} modules!")
    }
}