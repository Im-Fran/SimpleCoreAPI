package xyz.theprogramsrc.simplecoreapi.global.dependencydownloader

import com.google.gson.*
import org.json.XML
import java.net.URL

/**
 * Representation of Dependency
 * @param group The group id of the dependency
 * @param artifactId The artifact id of the dependency
 * @param version The version of the dependency
 * @param md5Hash The md5 hash of the dependency, if null the downloader will not check the md5 hash (Which is not recommended)
 */
data class Dependency(val group: String, val artifactId: String, val version: String, val md5Hash: String? = null)

/**
 * Representation of a Repository
 * @param url The url of the repository. Example: https://repo1.maven.org/maven2/
 */
data class Repository(val url: String) {

    private val mavenUrlFormat = "%s/%s/%s/%s-%s.jar"

    /**
     * Finds the artifact url to download
     * @return The artifact url to download
     */
    fun findArtifact(dependency: Dependency): String? {
        return try {
            val parsedVersion = if(dependency.version.endsWith("-SNAPSHOT")){
                parseSnapshotVersion(dependency)
            } else if(dependency.version == "LATEST") {
                try {
                    val result = JsonParser.parseString(XML.toJSONObject(URL("$url/${rewriteEscaping(dependency.group).replace('.','/')}/${dependency.artifactId}/maven-metadata.xml").readText()).toString())
                        .asJsonObject
                        .getAsJsonObject("metadata")
                        .getAsJsonObject("versioning")
                        .get("latest")
                        .asString
                    if(result.endsWith("-SNAPSHOT")) {
                        parseSnapshotVersion(Dependency(dependency.group, dependency.artifactId, result, dependency.md5Hash))
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
                rewriteEscaping(dependency.artifactId),
                dependency.version,
                rewriteEscaping(dependency.artifactId),
                parsedVersion
            )
            URL(result)
            result
        }catch (e: Exception){
            null
        }
    }

    private fun rewriteEscaping(data: String) = data.replace("{}", ".")

    private fun parseSnapshotVersion(dependency: Dependency): String? = try {
        val json = JsonParser.parseString(XML.toJSONObject(URL("$url/${rewriteEscaping(dependency.group).replace('.','/')}/${dependency.artifactId}/${dependency.version}/maven-metadata.xml").readText()).toString())
            .asJsonObject
            .getAsJsonObject("metadata")
            .getAsJsonObject("versioning")
            .getAsJsonObject("snapshot")
        dependency.version.replace("-SNAPSHOT", "-${json.get("timestamp").asString}-${json.get("buildNumber").asString}")
    }catch (e: Exception){
        null
    }
}