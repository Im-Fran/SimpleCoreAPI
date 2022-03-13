package xyz.theprogramsrc.simplecoreapi.global.module

import com.google.gson.JsonParser
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.URL
import java.security.MessageDigest
import java.util.*
import java.util.jar.JarFile

/**
 * Module Helper to Download or Sort modules
 */
object ModuleHelper {

    /**
     * Downloads a Module from the database
     * @param repositoryId Identifier of the artifact inside the repository
     * @param downloadLocation Location to download the module. (Defaults to plugins/SimpleCoreAPI/modules/)
     * @return true if the module was downloaded, false otherwise
     */
    fun downloadModule(repositoryId: String, downloadLocation: File = File("plugins/SimpleCoreAPI/modules/")): Boolean{
        if(!downloadLocation.exists()) downloadLocation.mkdirs()
        validateRepositories()
        val repo = (JsonParser.parseString(File("plugins/SimpleCoreAPI/repositories.json").readText()).asJsonArray.firstOrNull { element ->
            JsonParser.parseString(URL("https://${parseHost(element.asJsonObject.get("url").asString)}/service/rest/v1/search?repository=${element.asJsonObject.get("repo").asString}&format=maven2&maven.artifactId=$repositoryId&maven.extension=jar&sort=version").readText()).asJsonObject.get("items").asJsonArray.size() > 0
        } ?: return false).asJsonObject
        val artifact = ((JsonParser.parseString(URL("https://${parseHost(repo.get("url").asString)}/service/rest/v1/search/?repository=${repo.get("repo").asString}&format=maven2&maven.artifactId=$repositoryId&maven.extension=jar&sort=version").readText()).asJsonObject.get("items").asJsonArray.firstOrNull { it.asJsonObject.get("repository").asString.equals(repo.get("repo").asString) } ?: return false).asJsonObject.get("assets").asJsonArray.firstOrNull {
            if(!it.asJsonObject.get("maven2").asJsonObject.get("extension").asString.equals("jar")){
                return@firstOrNull false // Check that this is a jar file (not checksums or pom)
            }

            if(it.asJsonObject.get("maven2").asJsonObject.has("classifier")){
                if(it.asJsonObject.get("maven2").asJsonObject.get("classifier").asString.equals("sources")){
                    return@firstOrNull false // Check that this is not a sources jar file
                }

                if(it.asJsonObject.get("maven2").asJsonObject.get("classifier").asString.equals("javadoc")){
                    return@firstOrNull false // Check that this is not a javadoc jar file
                }
            }

            if(!it.asJsonObject.get("maven2").asJsonObject.get("artifactId").asString.equals(repositoryId)) {
                return@firstOrNull false // Check that this is the correct artifact
            }

            if(!it.asJsonObject.get("contentType").asString.equals("application/java-archive")){
                return@firstOrNull false // Check that this is a jar file
            }

            return@firstOrNull true
        } ?: return false).asJsonObject
        return artifact.get("downloadUrl").asString.let {
            val bytes = URL(it).readBytes()
            val sha512 = MessageDigest.getInstance("SHA-512").digest(bytes)
            val file = File(downloadLocation, "$repositoryId.jar")
            file.writeBytes(bytes)
            val fileSha512 = MessageDigest.getInstance("SHA-512").digest(file.readBytes())
            Arrays.equals(sha512, fileSha512)
        }
    }

    /**
     * Generate a new list with the correct order to load the modules
     * @param dependencies List of dependencies
     * @return List of the sorted modules to load
     */
    fun sortModuleDependencies(dependencies: Map<String, Collection<String>>): List<String> {
        val sorted = mutableListOf<String>()
        // First add the modules that don't have dependencies
        sorted.addAll(dependencies.filter { it.value.isEmpty() }.keys)

        // Now add the modules that have dependencies
        dependencies.filter { !sorted.contains(it.key) }.forEach { (moduleName, moduleDepends) ->
            moduleDepends.forEach { dependency ->
                if(sorted.contains(dependency) && sorted.indexOf(moduleName) > sorted.indexOf(dependency)) {
                    sorted.remove(dependency)
                    sorted.add(sorted.indexOf(moduleName), dependency)
                }else if(!sorted.contains(dependency)){
                    sorted.add(dependency)
                }
            }
            if(!sorted.contains(moduleName)) sorted.add(moduleName)
        }
        return sorted.filter { it.isNotBlank() && it.isNotEmpty() }
    }

    /**
     * Ensures that the repositories are up-to-date
     */
    private fun validateRepositories(){
        val file = File("plugins/SimpleCoreAPI/repositories.json")
        val onlineBytes = URL("https://raw.githubusercontent.com/TheProgramSrc/PluginsResources/master/SimpleCoreAPI/repositories.json").readBytes()
        if(!file.exists()) file.createNewFile()
        file.writeBytes(onlineBytes) // Always overwrite the file
    }

    /**
     * Parses the host from a URL
     * @param url URL to parse
     * @return Host of the URL
     */
    private fun parseHost(url: String): String = if(url.startsWith("http")) url.split("://")[1].split("/")[0] else url.split("/")[0]

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
     * Scans the given [File] for the simplecoreapi.modules
     * file and loads the required modules if any
     * @param file File to scan.
     * @param downloadLocation Location to download the modules. (Defaults to plugins/SimpleCoreAPI/modules/)
     */
    fun downloadRequiredModules(file: File, downloadLocation: File = File("plugins/SimpleCoreAPI/modules/")){
        if(file.extension != "jar") return
        try {
            JarFile(file).use { jarFile ->
                val jarEntry = jarFile.getJarEntry("simplecoreapi.modules")
                if (jarEntry != null) {
                    val inputStream = jarFile.getInputStream(jarEntry)
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    reader.readLines().forEach {
                        if(it.isNotBlank() && it.isNotEmpty() && !it.startsWith("#")) {
                            if(!File(downloadLocation, "$it.jar").exists()){
                                downloadModule(it)
                            }
                        }
                    }
                }
            }
        } catch (ignored: Exception) {}
    }
}