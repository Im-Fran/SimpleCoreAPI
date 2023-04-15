package xyz.theprogramsrc.simplecoreapi.global.module

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import xyz.theprogramsrc.simplecoreapi.global.SimpleCoreAPI
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.URL
import java.util.jar.JarFile

/**
 * Module Helper to Download or Sort modules
 */
object ModuleHelper {

    private var lastRepoUpdate = 0L // Used to avoid timeouts

    /**
     * Downloads a Module from the database
     * @param repository Repository of a module to download. (Must be in GitHub format 'User/Repository'. Example: 'TheProgramSrc/SimpleCore-UIsModule')
     * @param fileName The name of the file. This can be fetched using the repository metadata.
     * @param downloadLocation Location to download the module. (Defaults to SimpleCoreAPI/modules/)
     * @return true if the module was downloaded, false otherwise
     */
    fun downloadModule(repository: String, fileName: String, downloadLocation: File = SimpleCoreAPI.dataFolder("modules/")): Boolean{
        if(!downloadLocation.exists()) downloadLocation.mkdirs()
        val releases = JsonParser.parseString(URL("https://api.github.com/repos/$repository/releases").readText()).asJsonArray // Get the repo releases list
        if(releases.isEmpty) // If empty stop
            return false
        val latestRelease = releases[0].asJsonObject
        val assets = JsonParser.parseString(URL(latestRelease.get("assets_url").asString).readText()).asJsonArray // List all the available assets
        if(assets.isEmpty)
            return false
        assets.find { it.asJsonObject.get("name").asString.endsWith(".jar") }?.asJsonObject?.get("browser_download_url")?.asString.let { // Find the first asset that's a .jar (Should be only one, but let's check just in case)
            val bytes = URL(it).readBytes() // Read bytes
            val file = File(downloadLocation, "$fileName.jar") // Create the file
            if(!file.exists()) file.createNewFile()
            file.writeBytes(bytes) // Overwrite the bytes with the new data
        }
        return true // At this point everything went well!
    }

    /**
     * Generate a new list with the correct order to load the modules
     * @param dependencies List of dependencies
     * @return List of the sorted modules to load
     */
    fun sortModuleDependencies(dependencies: Map<String, Collection<String>>): List<String> {
        val visited = mutableSetOf<String>()
        val result = mutableListOf<String>()

        fun dfs(node: String) {
            visited.add(node)
            for (neighbor in dependencies[node] ?: emptySet()) {
                if (neighbor !in visited) {
                    dfs(neighbor)
                }
            }

            result.add(node)
        }

        for (node in dependencies.keys) {
            if (node !in visited) {
                dfs(node)
            }
        }

        return result
    }

    /**
    * Scans the given folder for jar files and then scan
    * every jar file to download the required modules
    */
    fun scanRequiredModules(folder: File = File(".")): Unit = (folder.listFiles() ?: emptyArray()).forEach {
        if(it.isDirectory) {
            scanRequiredModules(it)
        }else if(it.extension == "jar") {
            downloadRequiredModules(it)
        }
    }

    /**
    * Updates the modules repository cache
    */
    fun updateRepository(){
        // To allow the development of modules and testing we'll let devs provide the environment variable 'SCAPI_NO_REPO_UPDATE'
        if(System.getenv("SCAPI_NO_REPO_UPDATE") != null)
            return

        val now = System.currentTimeMillis()
        if(lastRepoUpdate == 0L || (lastRepoUpdate - now) > 30000L){
            val file = SimpleCoreAPI.dataFolder("modules-repository.json")
            val onlineBytes = URL("https://github.com/TheProgramSrc/GlobalDatabase/raw/master/SimpleCoreAPI/modules-repository.json").readBytes() // Get the online version
            if(!file.exists()) file.createNewFile() // Create the file
            file.writeBytes(onlineBytes) // Overwrite file
            lastRepoUpdate = now // Update the update time
        }
    }

    /**
     * Gets the module metadata from the repository
     * @param moduleId The id of the module to fetch the metadata
     * @return the given module metadata if it's under the modules reposutory, otherwise null.
     */
    fun getModuleMeta(moduleId: String): JsonObject? {
        updateRepository() // First we update the repo
        val json = JsonParser.parseString(SimpleCoreAPI.dataFolder("modules-repository.json").readText()).asJsonObject
        return if(json.has(moduleId)) json.getAsJsonObject(moduleId) else null
    }

    /**
     * Scans the given [File] for the simplecoreapi.modules
     * file and loads the required modules if any
     * @param file File to scan.
     * @param downloadLocation Location to download the modules. (Defaults to SimpleCoreAPI/modules/)
     */
    fun downloadRequiredModules(file: File, downloadLocation: File = SimpleCoreAPI.dataFolder("modules/")){
        updateRepository() // First we update the repository
        if(file.extension != "jar") return
        try {
            JarFile(file).use { jarFile -> // Now we check for every file
                val jarEntry = jarFile.getJarEntry("simplecoreapi.modules") // If we find simplecoreapi.modules
                if (jarEntry != null) {
                    val inputStream = jarFile.getInputStream(jarEntry) // Read the file
                    val reader = BufferedReader(InputStreamReader(inputStream)) // Create the reader
                    reader.readLines().forEach { // Read every line
                        if(it.isNotBlank() && it.isNotEmpty() && !it.startsWith("#")) { // Check that is not a blank line nor a comment
                            val meta = getModuleMeta(it) // Fetch the metadata
                            if(meta != null){
                                if(!File(downloadLocation, "${meta.get("file_name").asString}.jar").exists()){
                                    val repo = if(meta.has("repository")) meta.get("repository").asString else "TheProgramSrc/SimpleCore-$it" // Generate default repo if not found
                                    downloadModule(repo, meta.get("file_name").asString) // Download the module
                                }
                            }
                        }
                    }
                }
            }
        } catch (ignored: Exception) {}
    }
}