package xyz.theprogramsrc.simplecoreapi.global.module

import xyz.theprogramsrc.simplecoreapi.global.exceptions.InvalidModuleDescriptionException
import xyz.theprogramsrc.simplecoreapi.global.exceptions.InvalidModuleException
import xyz.theprogramsrc.simplecoreapi.global.exceptions.ModuleDownloadException
import xyz.theprogramsrc.simplecoreapi.global.exceptions.ModuleLoadException
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.URLClassLoader
import java.nio.file.Files
import java.util.*
import java.util.jar.JarFile
import java.util.jar.JarInputStream
import java.util.logging.Logger
import java.util.zip.ZipEntry


class ModuleManager(private val logger: Logger) {

    private val modulesFolder = File("plugins/SimpleCoreAPI/modules")
    private val updatesFolder = File("plugins/SimpleCoreAPI/update")
    private var loadedModules = mutableMapOf<String, Module>()

    init {
        if (!modulesFolder.exists()) modulesFolder.mkdirs()
        if (!updatesFolder.exists()) updatesFolder.mkdirs()
    }

    companion object {
        private var isLoaded = false

        fun init(logger: Logger): ModuleManager {
            check(!isLoaded) { "ModuleManager is already loaded!" }
            isLoaded = true
            val moduleManager = ModuleManager(logger)
            moduleManager.load()
            return moduleManager
        }
    }

    /**
     * Gets a module from the loaded modules
     * @param name The name of the module
     * @return The requested module, or null if is not found or enabled
     */
    fun getModule(name: String): Module? = loadedModules[name]

    private fun load() {
        val files = (modulesFolder.listFiles() ?: emptyArray()).filter { it.name.endsWith(".jar") }
        if (files.isEmpty()) return
        val modules = mutableMapOf<String, Pair<File, ModuleDescription>>()
        val dependencies = mutableListOf<String>()
        val loadedModules = mutableSetOf<String>()

        // First we update the module jars
        updateJars()

        // Now we load and save all the module descriptions from the available modules
        files.forEach { file ->
            try {
                // Validate that this file is a module
                val props = loadDescription(file) ?: throw InvalidModuleDescriptionException("Failed to load module description for " + file!!.name)
                val required = arrayOf("main", "name", "version", "author", "description")
                for (req in required) {
                    if (!props.containsKey(req)) {
                        throw InvalidModuleDescriptionException("Missing required property " + req + " in module " + file!!.name)
                    }
                }

                // Load the module description
                val description = ModuleDescription(
                    props.getProperty("main").replace("\"(.+)\"".toRegex(), "$1"),
                    props.getProperty("name").replace("\"(.+)\"".toRegex(), "$1"),
                    props.getProperty("version").replace("\"(.+)\"".toRegex(), "$1"),
                    props.getProperty("author").replace("\"(.+)\"".toRegex(), "$1"),
                    props.getProperty("description").replace("\"(.+)\"".toRegex(), "$1"),
                    (if (props.containsKey("dependencies")) props.getProperty("dependencies") else "").replace(
                        "\"(.+)\"".toRegex(),
                        "$1"
                    ).split(",")
                        .toTypedArray()
                )

                // Validate the module name
                if (description.name.indexOf(' ') != -1) {
                    throw InvalidModuleDescriptionException("Module name cannot contain spaces!")
                }

                // Save to load later
                modules[description.name] = Pair(file, description)

                // Save the dependencies to download the missing ones
                description.dependencies.forEach { dependency ->
                    if (!dependencies.contains(dependency)) {
                        dependencies.add(dependency)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Loop through the dependencies and download the missing ones
        val downloadedModules: MutableList<String> = ArrayList()
        for (module in dependencies.filter { it.isNotBlank() }.filter { !modules.containsKey(it) }) {
            if (ModuleHelper.downloadModule(module)) {
                downloadedModules.add(module)
            } else {
                throw ModuleDownloadException("Failed to download module '$module'")
            }
        }

        // Load the modules again if there are new dependencies
        if (downloadedModules.size > 0) {
            load()
            return
        }

        // Sort the modules to load dependencies first
        val moduleDependencies = mutableMapOf<String, Collection<String>>()
        modules.forEach {
            moduleDependencies[it.key] = it.value.second.dependencies.toList()
        }

        ModuleHelper.sortModuleDependencies(moduleDependencies).forEach { moduleName ->
            if(!loadedModules.contains(moduleName) && modules.containsKey(moduleName)) {
                modules[moduleName]?.let { pair ->
                    if(loadIntoClasspath(pair.first, pair.second)){
                        loadedModules.add(moduleName)
                    }
                }
            }
        }
        logger.info("Successfully enabled ${this.loadedModules.size} modules.")
    }

    /**
     * Loads a module into the classpath
     * @param file The file to load
     * @param description The module description
     * @throws InvalidModuleException If the main module class is invalid
     * @throws ModuleLoadException If the module failed to load
     */
    @Throws(InvalidModuleException::class, ModuleLoadException::class)
    private fun loadIntoClasspath(file: File, description: ModuleDescription): Boolean {
        var entry: ZipEntry?
        try {
            URLClassLoader(arrayOf(file.toURI().toURL()), this.javaClass.classLoader).use { loader ->
                FileInputStream(file).use { fileInputStream ->
                    JarInputStream(fileInputStream).use { jarInputStream ->
                        while (jarInputStream.nextEntry.also { entry = it } != null) {
                            val entryName = entry?.name ?: continue
                            if (entryName.endsWith(".class")) {
                                val name = entryName.replace("/", ".").replace(".class", "")
                                val clazz = Class.forName(name, true, loader)
                                if (name == description.mainClass) {
                                    if (!Module::class.java.isAssignableFrom(clazz)) {
                                        throw InvalidModuleException("The class ${description.mainClass} must be extended to the Module class!")
                                    }
                                    return try {
                                        logger.info("Loading module ${description.name} v${description.version}")
                                        val moduleClass = clazz.asSubclass(Module::class.java)
                                        val module = moduleClass.getConstructor().newInstance()
                                        module.init(file, description)
                                        module.onEnable()
                                        loadedModules[description.name] = module
                                        logger.info("Module ${description.name} v${description.version} loaded!")
                                        true
                                    } catch (e: Exception) {
                                        throw ModuleLoadException("Failed to load module ${description.name} v${description.version}", e)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            throw ModuleLoadException("Failed to load module ${file.name}", e)
        }
        return false
    }

    /**
     * Loads (if possible) the module.properties file into a [Properties] object
     * @param from The file to load the properties from
     * @return The [Properties] object
     */
    private fun loadDescription(from: File?): Properties? {
        // Search the file 'module.properties' inside the jar file
        try {
            JarFile(from).use { jarFile ->
                val jarEntry = jarFile.getJarEntry("module.properties")
                if (jarEntry != null) {
                    val inputStream = jarFile.getInputStream(jarEntry)
                    val properties = Properties()
                    properties.load(inputStream)
                    return properties
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Disable all the loaded modules
     */
    fun disableModules() {
        loadedModules.values.forEach { it.onDisable() }
        loadedModules.clear()
        System.gc()
    }

    /**
     * Updates all the jars placed under the update/ folder
     */
    private fun updateJars(){
        updatesFolder.listFiles()?.filter { it.name.endsWith(".jar") }?.filter { loadDescription(it) != null }?.forEach {
            val name = loadDescription(it)?.getProperty("name") ?: return@forEach
            val outdatedFile = modulesFolder.listFiles()?.filter { it.name.endsWith(".jar") }?.firstOrNull { loadDescription(it)?.getProperty("name")?.equals(name) ?: false }?.toPath() ?: return@forEach
            Files.deleteIfExists(outdatedFile)
            Files.move(it.toPath(), outdatedFile)
        }
    }
}