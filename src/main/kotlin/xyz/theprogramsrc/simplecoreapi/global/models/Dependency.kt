package xyz.theprogramsrc.simplecoreapi.global.models

import xyz.theprogramsrc.simplecoreapi.global.SimpleCoreAPI
import xyz.theprogramsrc.simplecoreapi.global.utils.extensions.folder
import java.io.File
import java.net.URL
import java.security.MessageDigest

/**
 * Representation of Dependency
 * @param group The group id of the dependency
 * @param artifact The artifact id of the dependency
 * @param version The version of the dependency
 * @param md5Hash The md5 hash of the dependency, if null the downloader will not check the md5 hash (Which is not recommended)
 */
data class Dependency(val group: String, val artifact: String, val version: String, val md5Hash: String? = null) {

    companion object {
        private val librariesFolder = SimpleCoreAPI.dataFolder("libraries/")
        val dependencies = mutableListOf<Dependency>()

        /**
         * Adds a dependency to the list of dependencies
         * @param dependency The [Dependency] to load
         */
        fun addDependency(dependency: Dependency) {
            val found = dependencies.find { it.group == dependency.group && it.artifact == dependency.artifact }
            if (found == null) {
                dependencies.add(dependency)
                return
            }

            if (found.version != dependency.version) {
                SimpleCoreAPI.logger.warn("Dependency '${found.group}:${found.artifact}' already exists with version '${found.version}'!")
            }
        }
    }

    /**
     * Retrieves the [Dependency] file inside the local storage
     * @return The [Dependency] file
     */
    fun asFile(): File = File(librariesFolder, "$group-$artifact-$version.jar")

    /**
     * Downloads a [Dependency] into the local storage if is not already downloaded
     *
     * @return The [Dependency] file if is successfully downloaded, null otherwise
     */
    fun download(): File? {
        val file = asFile()

        if(!file.exists()){
            val repo = Repository.repositories.firstOrNull { it.findArtifact(this) != null } ?: return null
            val artifactUrl = repo.findArtifact(this) ?: return null
            val downloadBytes = URL(artifactUrl).readBytes()
            if(md5Hash != null){
                val digest = MessageDigest.getInstance("MD5")
                digest.reset()
                val downloadMd5 = digest.digest(downloadBytes).joinToString("") { "%02x".format(it) }
                if(downloadMd5 != md5Hash){
                    SimpleCoreAPI.logger.error("MD5 mismatch for $group:$artifact! Expected: '${md5Hash}', Got: '$downloadMd5'")
                    return null
                }
            }

            file.writeBytes(downloadBytes)
        }

        return file
    }
}