package xyz.theprogramsrc.simplecoreapi.global.dependencydownloader

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.apache.commons.io.FileUtils
import xyz.theprogramsrc.simplecoreapi.global.SimpleCoreAPI
import xyz.theprogramsrc.simplecoreapi.global.models.Dependency
import xyz.theprogramsrc.simplecoreapi.global.models.Repository
import xyz.theprogramsrc.simplecoreapi.global.utils.extensions.file
import java.io.File
import java.net.URL

class DependencyDownloader {

    private val logger = SimpleCoreAPI.logger
    private val librariesFolder = SimpleCoreAPI.dataFolder("libraries/")
    private val modulesRepositoryFile = SimpleCoreAPI.dataFolder(path = "modules-repository.json", asFolder = false)

    init {
        // Download and update the modules repository file
        updateModulesRepository()

        // Downloads the pending dependencies
        val pendingDependencies = Dependency.dependencies.filter { !it.asFile().exists() }
        if(pendingDependencies.isNotEmpty()) {
            logger.info("Downloading ${pendingDependencies.size} pending dependencies...")
            pendingDependencies.forEach { dependency ->
                logger.info("Downloading ${dependency.group}:${dependency.artifact}:${dependency.version}...")
                downloadDependency(dependency)
            }
        }
    }

    /**
     * Downloads a dependency
     * @param dependency The dependency to download
     */
    private fun downloadDependency(dependency: Dependency) {
        val destination = dependency.asFile()                           // Where to save the dependency
        val artifactUri = Repository.findArtifact(dependency)           // Get the repository where the dependency is located
        val url = URL(artifactUri)                                      // Create the URL to download the dependency
        val connection = url.openConnection()                           // Open the connection
        connection.setRequestProperty("User-Agent", "SimpleCoreAPI")    // Set the user agent
        connection.connect()                                            // Connect to the URL
        val contentLength = connection.contentLengthLong                // Get the content length
        val inputStream = connection.getInputStream()                   // Get the input stream
        val outputStream = destination.outputStream()                   // Get the output stream
        val buffer = ByteArray(1024)                              // Create a buffer
        var read: Int                                                   // Variable to store the read bytes
        var downloaded: Long = 0                                        // Variable to store the downloaded bytes
        while (inputStream.read(buffer).also { read = it } > 0) {       // While there are bytes to read
            outputStream.write(buffer, 0, read)                         // Write the bytes to the output stream
            downloaded += read                                          // Add the read bytes to the downloaded bytes
            val percentage = downloaded * 100 / contentLength            // Calculate the percentage
            logger.info("Downloading ${dependency.group}:${dependency.artifact}:${dependency.version}... $percentage%") // Log the percentage
        }
        outputStream.close()                                            // Close the output stream
        inputStream.close()                                             // Close the input stream
        logger.info("Downloaded ${dependency.group}:${dependency.artifact}:${dependency.version} to ${destination.relativeTo(File(".")).path}") // Log the download
    }

    /**
     * Checks for updates and removes any old dependency from the
     * libraries folder
     */
    private fun updateModulesRepository() {
        fun jsonDependToMap(json: JsonObject, map: MutableMap<String, String>) {
            json["dependencies"].asJsonArray.forEach { dependency ->
                val dependencyObject = dependency.asJsonObject
                map["${dependencyObject["group"].asString}:${dependencyObject["artifact"].asString}"] = dependencyObject["version"].asString
            }
        }

        val cachedDependencies = mutableMapOf<String, String>()
        val onlineDependencies = mutableMapOf<String, String>()
        if(modulesRepositoryFile.exists() && modulesRepositoryFile.readText().isNotBlank()) {
            val cachedJson = JsonParser.parseString(modulesRepositoryFile.readText()).asJsonObject
            jsonDependToMap(cachedJson, cachedDependencies)
        }

        downloadModulesRepository()

        val downloadedJson = JsonParser.parseString(modulesRepositoryFile.readText()).asJsonObject
        jsonDependToMap(downloadedJson, onlineDependencies)

        cachedDependencies.forEach { (key, version) ->
            val newVersion = onlineDependencies[key]
            if(newVersion != null && newVersion != version) {
                logger.info("Found update for dependency $key from $version to $newVersion. Removing old file to download update...")
                val (group, artifact) = key.split(":")
                val file = File(librariesFolder, "$group-$artifact-$version.jar")
                if(file.exists()) {
                    FileUtils.forceDelete(file)
                }
            }
        }
    }

    /**
     * Downloads the modules repository file from the GitHub repository.
     * If the download fails, it will create a new file with the default content (empty repositories and dependencies)
     */
    private fun downloadModulesRepository() {
        val destination = modulesRepositoryFile.file()
        val content = try {
            URL("https://raw.githubusercontent.com/TheProgramSrc/GlobalDatabase/master/SimpleCoreAPI/modules-repository.json").readBytes()
        } catch (_: Exception) {
            JsonObject().apply {
                add("repositories", JsonArray().apply {
                    add("repositories", JsonArray().apply {
                        add("https://s01.oss.sonatype.org/content/groups/public/")
                        add("https://oss.sonatype.org/content/groups/public")
                        add("https://oss.sonatype.org/content/repositories/snapshots/")
                        add("https://oss.sonatype.org/content/repositories/releases/")
                        add("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
                        add("https://repo.papermc.io/repository/maven-public/")
                        add("https://repo.codemc.io/repository/maven-public/")
                        add("https://jitpack.io/")
                    })
                })
                add("dependencies", JsonArray())
            }.toString().toByteArray()
        }
        destination.writeBytes(content)
    }

}