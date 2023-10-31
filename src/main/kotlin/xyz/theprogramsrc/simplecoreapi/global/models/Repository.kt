package xyz.theprogramsrc.simplecoreapi.global.models

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.json.XML
import xyz.theprogramsrc.simplecoreapi.global.SimpleCoreAPI
import java.net.URL

/**
 * Representation of a Repository
 * @param url The url of the repository. Example: https://repo1.maven.org/maven2/
 */
data class Repository(val url: String) {

    companion object {
        val repositories = mutableListOf<Repository>()

        /**
         * Adds a repository to the list of repositories
         * @param repository The repository to add
         */
        fun addRepository(repository: Repository) {
            val isValidURL = try {
                URL(repository.url)
                true
            }catch (_: Exception){
                false
            }

            if(!isValidURL) {
                SimpleCoreAPI.logger.error("Repository ${repository.url} must have a valid url!")
                return
            }

            if(repositories.any { it.url == repository.url }) {
                SimpleCoreAPI.logger.warn("Repository ${repository.url} already exists!")
            }

            repositories.add(repository)
        }

        fun findArtifact(dependency: Dependency): String? {
            for(repository in repositories){
                val result = repository.findArtifact(dependency)
                if(result != null) return result
            }
            return null
        }
    }

    private val mavenUrlFormat = "%s/%s/%s/%s-%s.jar"

    /**
     * Finds the artifact url to download
     * @return The artifact url to download
     */
    fun findArtifact(dependency: Dependency): String? {
        val cachedResolved = SimpleCoreAPI.dataFolder("dependency-resolutions.cache.json")
        if(cachedResolved.exists() && cachedResolved.readText().isNotBlank()) {
            val json = JsonParser.parseString(cachedResolved.readText()).asJsonObject
            if(json.has(dependency.toString())) {
                return json.get(dependency.toString()).asString
            }
        }

        val resolution = try {
            val parsedVersion = if(dependency.version.endsWith("-SNAPSHOT")){
                parseSnapshotVersion(dependency)
            } else if(dependency.version == "LATEST") {
                try {
                    val result = JsonParser.parseString(XML.toJSONObject(URL("$url/${rewriteEscaping(dependency.group).replace('.','/')}/${dependency.artifact}/maven-metadata.xml").readText()).toString())
                        .asJsonObject
                        .getAsJsonObject("metadata")
                        .getAsJsonObject("versioning")
                        .get("latest")
                        .asString
                    if(result.endsWith("-SNAPSHOT")) {
                        parseSnapshotVersion(Dependency(dependency.group, dependency.artifact, result, dependency.md5Hash))
                    }else {
                        result
                    }
                }catch (e: Exception){
                    null
                }
            }else{
                dependency.version
            } ?: return null

            val result = url + String.format(mavenUrlFormat,
                rewriteEscaping(dependency.group).replace('.', '/'),
                rewriteEscaping(dependency.artifact),
                dependency.version,
                rewriteEscaping(dependency.artifact),
                parsedVersion
            )
            URL(result)
            result
        }catch (e: Exception){
            null
        }

        if(resolution != null) {
            val json = if(cachedResolved.exists() && cachedResolved.readText().isNotBlank()) {
                JsonParser.parseString(cachedResolved.readText()).asJsonObject
            }else{
                JsonObject()
            }
            json.addProperty(dependency.toString(), resolution)
            cachedResolved.writeText(json.toString())
        }

        return resolution
    }

    private fun rewriteEscaping(data: String) = data.replace("{}", ".")

    private fun parseSnapshotVersion(dependency: Dependency): String? = try {
        val json = JsonParser.parseString(XML.toJSONObject(URL("$url/${rewriteEscaping(dependency.group).replace('.','/')}/${dependency.artifact}/${dependency.version}/maven-metadata.xml").readText()).toString())
            .asJsonObject
            .getAsJsonObject("metadata")
            .getAsJsonObject("versioning")
            .getAsJsonObject("snapshot")
        dependency.version.replace("-SNAPSHOT", "-${json.get("timestamp").asString}-${json.get("buildNumber").asString}")
    }catch (e: Exception){
        null
    }
}