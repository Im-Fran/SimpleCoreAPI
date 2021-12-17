package xyz.theprogramsrc.simplecoreapi.global.module

import com.google.gson.JsonParser
import java.io.File
import java.net.URL
import java.security.MessageDigest
import java.util.*

/**
 * Module Helper to Download or Sort modules
 */
object ModuleHelper {
    private val downloadLocation = File("plugins/SimpleCoreAPI/modules/")

    /**
     * Downloads a Module from the database
     * @param repositoryId Identifier of the artifact inside the repository
     * @return true if the module was downloaded, false otherwise
     */
    fun downloadModule(repositoryId: String): Boolean{
        if(!downloadLocation.exists()) downloadLocation.mkdirs()
        validateRepositories()
        val repo = (JsonParser().parse(File("plugins/SimpleCoreAPI/repositories.json").readText()).asJsonArray.firstOrNull { element ->
            JsonParser().parse(URL("https://${parseHost(element.asJsonObject.get("url").asString)}/service/rest/v1/search?repository=${element.asJsonObject.get("repo").asString}&format=maven2&maven.artifactId=$repositoryId&maven.extension=jar&sort=version").readText()).asJsonObject.get("items").asJsonArray.size() > 0
        } ?: return false).asJsonObject
        val artifact = ((JsonParser().parse(URL("https://${parseHost(repo.get("url").asString)}/service/rest/v1/search/?repository=${repo.get("repo").asString}&format=maven2&maven.artifactId=$repositoryId&maven.extension=jar&sort=version").readText()).asJsonObject.get("items").asJsonArray.firstOrNull { it.asJsonObject.get("repository").asString.equals(repo.get("repo").asString) } ?: return false).asJsonObject.get("assets").asJsonArray.firstOrNull {
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
        dependencies.forEach { (moduleName, moduleDepends) ->
            moduleDepends.forEach {
                if(sorted.contains(it) && sorted.indexOf(moduleName) > sorted.indexOf(it)) {
                    sorted.remove(it)
                    sorted.add(sorted.indexOf(moduleName), it)
                }else if(!sorted.contains(it)){
                    sorted.add(it)
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
}