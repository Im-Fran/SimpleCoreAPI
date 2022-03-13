package xyz.theprogramsrc.simplecoreapi.global.module

import xyz.theprogramsrc.simplecoreapi.global.GitHubUpdateChecker
import xyz.theprogramsrc.simplecoreapi.global.exceptions.*
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

    private var config = mutableMapOf<String, String>()

    fun loadConfig(){
        config = File("plugins/SimpleCoreAPI/Settings.yml").apply {
            if(!exists()){
                parentFile.mkdirs()
                createNewFile()
            }
        }.let { f ->
            f.readLines().filter { !it.startsWith("#") }.map { it.split(": ") }.filter { it.size == 2 }.associate { it[0] to it[1] }
        }.toMutableMap()
    }

    fun saveConfig(){
        File("plugins/SimpleCoreAPI/Settings.yml").apply {
            if(!exists()){
                parentFile.mkdirs()
                createNewFile()
            }
        }.let {
            val lines = it.readLines().toMutableList()
            lines.forEachIndexed { index, line ->
                this.config.forEach { (key,value) ->
                    if(line.startsWith("$key: ")){
                        lines[index] = "$key: $value"
                    }
                }
            }
            it.writeBytes(lines.joinToString("\n").toByteArray())
        }
    }

    init {
        loadConfig()
        if(!config.containsKey("auto-update")){ // Load defaults
            config["auto-update"] = "false"
            saveConfig()
        }
    }

    /**
     * Gets a module from the loaded modules
     * @param name The name of the module
     * @return The requested module, or null if is not found or enabled
     */
    fun getModule(name: String): Module? = loadedModules[name]

    private fun load() {
        ModuleHelper.scanRequiredModules()
        val files = (modulesFolder.listFiles() ?: emptyArray()).filter { it.name.endsWith(".jar") }
        if (files.isEmpty()) return
        val modules = mutableMapOf<File, ModuleDescription>()
        val dependencies = mutableListOf<String>() // Used to download missing dependencies
        val loadedModules = mutableSetOf<String>() // Used to check if a module is already loaded

        // First we update the module jars (moving the ones from update/ to the modules/ folder)
        updateJars()

        // Now we load and save all the module descriptions from the available modules
        val updatedModules = mutableListOf<String>()
        files.forEach { file ->
            try {
                // Validate that this file is a module
                val props = loadDescription(file) ?: throw InvalidModuleDescriptionException("Failed to load module description for " + file!!.name)
                val required = arrayOf("main", "name", "version", "author", "description", "repository-id")
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
                    props.getProperty("repository-id").replace("\"(.+)\"".toRegex(), "$1"),
                    (if (props.containsKey("dependencies")) props.getProperty("dependencies") else "").replace(
                        "\"(.+)\"".toRegex(),
                        "$1"
                    ).split(",")
                        .filter { it.isNotBlank() }
                        .toTypedArray(),
                    props.getProperty("github-repository") ?: "",
                )

                if(description.githubRepository.isNotBlank()){
                    // Check for updates
                    val checker = GitHubUpdateChecker(logger, description.githubRepository, description.version) // Generate a new update checker
                    val isAvailable = checker.checkForUpdates() // Check for the updates
                    val autoUpdate = config["auto-update"] == "true" // Check if we have enabled the auto updater
                    if(isAvailable && autoUpdate){ // Download an update if there is one available and the auto updater is enabled
                        logger.info("An update for the module ${description.name} is available. Downloading and updating...")
                        if(ModuleHelper.downloadModule(description.repositoryId, File("plugins/SimpleCoreAPI/update/").apply { if(!exists()) mkdirs() })){
                            logger.info("Successfully updated the module ${description.name}")
                            updatedModules.add(description.name)
                        } else {
                            logger.severe("Failed to update the module ${description.name}. Please download manually from https://github.com/${description.githubRepository}/releases/latest")
                        }
                    } else if(isAvailable){ // Notify the user that an update is available
                        checker.checkWithPrint()
                    }
                }

                // Validate the module name
                if (description.name.indexOf(' ') != -1) {
                    throw InvalidModuleDescriptionException("Module name cannot contain spaces!")
                }

                // Save to load later
                modules[file] = description

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

        if(updatedModules.isNotEmpty()){
            load()
            return
        }

        // Loop through the dependencies and download the missing ones
        val downloadedModules: MutableList<String> = ArrayList()
        for (dependencyId in dependencies.filter { it.isNotBlank() && !modules.any { entry -> entry.value.repositoryId == it } }) {
            if (ModuleHelper.downloadModule(dependencyId)) {
                downloadedModules.add(dependencyId)
            } else {
                throw ModuleDownloadException("Failed to download module with id '$dependencyId'")
            }
        }

        // Load the modules again if there are new dependencies
        if (downloadedModules.isNotEmpty()) {
            load()
            return
        }

        // Sort the modules to load dependencies first
        val moduleDependencies = mutableMapOf<String, Collection<String>>()
        modules.values.forEach {
            moduleDependencies[it.repositoryId] = it.dependencies.toList()
        }

        val urlClassLoader = URLClassLoader(modules.map { it.key }.map { it.toURI().toURL() }.toTypedArray(), this::class.java.classLoader)
        val sorted = ModuleHelper.sortModuleDependencies(moduleDependencies).filter { it.isNotBlank() }

        sorted.forEach { moduleName ->
            if(!loadedModules.contains(moduleName)) {
                modules.entries.firstOrNull { it.value.repositoryId == moduleName }?.let { entry ->
                    try {
                        loadIntoClasspath(urlClassLoader, entry.key, entry.value)
                    } catch (e: InvalidModuleException) {
                        e.printStackTrace()
                    } catch (e: ModuleLoadException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        logger.info("Successfully enabled ${this.loadedModules.size} modules.")
    }

    /**
     * Loads a module into the classpath
     * @param loader The [URLClassLoader] to load the module into
     * @param file The file to load
     * @param description The module description
     * @throws InvalidModuleException If the main module class is invalid
     * @throws ModuleLoadException If the module failed to load
     */
    @Throws(InvalidModuleException::class, ModuleLoadException::class)
    private fun loadIntoClasspath(loader: URLClassLoader, file: File, description: ModuleDescription) {
        var entry: ZipEntry?
        try {
            JarInputStream(FileInputStream(file)).use { jarInputStream ->
                val mainClass = loader.loadClass(description.mainClass)
                if(!Module::class.java.isAssignableFrom(mainClass)){
                    throw InvalidModuleException("The class ${description.mainClass} must be extended to the Module class!")
                }

                while (jarInputStream.nextEntry.also { entry = it } != null) {
                    val entryName = entry?.name ?: continue
                    if(!entryName.endsWith(".class")) continue
                    loader.loadClass(entryName.replace('/', '.').replace(".class", ""))
                }

                try {
                    logger.info("Loading module ${description.name} v${description.version}")
                    val start = System.currentTimeMillis()
                    val moduleClass = mainClass.asSubclass(Module::class.java)
                    val module = moduleClass.getConstructor().newInstance()
                    module.init(file, description)
                    module.onEnable()
                    loadedModules[description.repositoryId] = module
                    logger.info("Module ${description.name} v${description.version} loaded! (${System.currentTimeMillis() - start}ms)")
                } catch (e: Exception) {
                    throw ModuleLoadException("Failed to load module ${description.name} v${description.version}", e)
                }
            }
        } catch (e: Exception) {
            throw ModuleLoadException("Failed to load module ${file.name}", e)
        }
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
            val outdatedFile = modulesFolder.listFiles()?.filter { jar -> jar.name.endsWith(".jar") }?.firstOrNull { jar -> loadDescription(jar)?.getProperty("name")?.equals(name) ?: false }?.toPath() ?: return@forEach
            val newFile = File(modulesFolder.absolutePath, it.name)
            Files.deleteIfExists(outdatedFile)
            Files.move(it.toPath(), newFile.toPath())
        }
    }
}