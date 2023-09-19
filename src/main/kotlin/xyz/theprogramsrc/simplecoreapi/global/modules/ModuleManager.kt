package xyz.theprogramsrc.simplecoreapi.global.modules

import com.google.gson.JsonParser
import xyz.theprogramsrc.simplecoreapi.bungee.BungeeLoader
import xyz.theprogramsrc.simplecoreapi.global.SimpleCoreAPI
import xyz.theprogramsrc.simplecoreapi.global.utils.SoftwareType
import xyz.theprogramsrc.simplecoreapi.spigot.SpigotLoader
import xyz.theprogramsrc.simplecoreapi.standalone.StandaloneLoader
import java.io.File
import java.net.URL

object ModuleManager {

    /**
     * Downloads the given module from the GitHub releases.
     *
     * @param moduleId The module id (format: <author>/<repo>)
     * @param version The version to download. If null, the latest release will be downloaded
     *
     * @return The downloaded file
     */
    fun downloadModule(moduleId: String, version: String? = null): File? = try {
        assert(moduleId.split("/").size == 2) { "Invalid repositoryId format. It should be <author>/<repo>"}

        val releaseManifest = URL(if (version == null) "https://api.github.com/repos/$moduleId/releases/latest" else "https://api.github.com/repos/$moduleId/releases/tags/$version" ).let {
            JsonParser.parseReader(it.openStream().reader()).asJsonObject
        }

        val assets = releaseManifest.get("assets").asJsonArray
        // Sort by created_at (newest first) and filter by file name ending with .jar
        val asset = assets.sortedByDescending { it.asJsonObject.get("created_at").asString }.firstOrNull { it.asJsonObject.get("name").asString.endsWith(".jar") } ?: throw NullPointerException("No jar file found in the latest release of $moduleId")
        val downloadUrl = asset.asJsonObject.get("browser_download_url").asString
        val file = File(if(SimpleCoreAPI.instance.let { it.isRunningSoftwareType(SoftwareType.STANDALONE) || it.isRunningSoftwareType(SoftwareType.UNKNOWN) }) SimpleCoreAPI.dataFolder("modules/") else File("plugins/"), moduleId.substringAfterLast("/"))
        if(!file.exists()){
            file.createNewFile()
        }

        file.writeBytes(URL(downloadUrl).readBytes())
        file
    } catch(e: Exception) {
        e.printStackTrace()
        null
    }

    /**
     * Loads the given module file.
     * If running in standalone mode the module will be loaded later.
     * If running in bukkit/spigot/paper/purpur mode the module will be loaded using the bukkit class loader.
     * If running in bungee mode will be loaded using bungee
     *
     * @param file The module file
     */
    fun loadModule(file: File): Boolean = when(SimpleCoreAPI.instance.softwareType) {
        SoftwareType.STANDALONE -> {
            true // Is automatically loaded later
        }
        SoftwareType.BUKKIT,
        SoftwareType.SPIGOT,
        SoftwareType.PAPER,
        SoftwareType.PURPUR, -> {
            // Load the module using bukkit
            SpigotLoader.instance.pluginLoader.loadPlugin(file).apply {
                onLoad()
                SpigotLoader.instance.pluginLoader.enablePlugin(this@apply)
            }
            true
        }
        SoftwareType.BUNGEE -> {
            BungeeLoader.instance.proxy.pluginManager.detectPlugins(BungeeLoader.instance.proxy.pluginsFolder) // Try to detect any new plugins. This is needed because the plugin manager is not updated when a plugin is loaded
            false
        }
        else -> {
            false
        }
    }
}