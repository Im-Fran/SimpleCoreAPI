package xyz.theprogramsrc.simplecoreapi.global.downloader

import com.google.gson.JsonParser
import xyz.theprogramsrc.simplecoreapi.global.SimpleCoreAPI
import java.io.File
import java.net.URL
import java.security.MessageDigest

object ModuleDownloader {

    fun download(repositoryId: String, version: String? = null): File? = try {
        val releaseManifest = URL(if (version == null) "https://api.github.com/repos/$repositoryId/releases/latest" else "https://api.github.com/repos/$repositoryId/releases/tags/$version" ).let {
            JsonParser.parseReader(it.openStream().reader()).asJsonObject
        }

        val assets = releaseManifest.get("assets").asJsonArray
        // Sort by created_at (newest first) and filter by file name ending with .jar
        val asset = assets.sortedByDescending { it.asJsonObject.get("created_at").asString }.firstOrNull { it.asJsonObject.get("name").asString.endsWith(".jar") } ?: throw NullPointerException("No jar file found in the latest release of $repositoryId")
        val downloadUrl = asset.asJsonObject.get("browser_download_url").asString
        val file = File(SimpleCoreAPI.dataFolder(), asset.asJsonObject.get("name").asString)
        if(!file.exists()){
            file.createNewFile()
        }

        file.writeBytes(URL(downloadUrl).readBytes())
        file
    } catch(e: Exception) {
        e.printStackTrace()
        null
    }
}