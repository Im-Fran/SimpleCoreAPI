package xyz.theprogramsrc.simplecoreapi.global.downloader

import com.google.gson.JsonParser
import xyz.theprogramsrc.simplecoreapi.global.SimpleCoreAPI
import java.io.File
import java.net.URL

object ModuleDownloader {

    private val modules = mutableListOf<RemoteModule>()

    init {
        // First we fetch the modules-repository.json from our global database (https://raw.githubusercontent.com/TheProgramSrc/GlobalDatabase/master/SimpleCoreAPI/modules-repository.json)
        val dataFolder = SimpleCoreAPI.dataFolder()
        val modulesRepository = File(dataFolder, "modules-repository.json")
        if(!modulesRepository.exists()){
            modulesRepository.createNewFile()
        }

        val remoteBytes = URL("https://raw.githubusercontent.com/TheProgramSrc/GlobalDatabase/master/SimpleCoreAPI/modules-repository.json").readBytes()
        val localBytes = modulesRepository.readBytes()
        // If they're not the same, we update the local file
        if(!remoteBytes.contentEquals(localBytes)){
            modulesRepository.writeBytes(remoteBytes)
        }

        // Read as json
        val json = JsonParser.parseReader(modulesRepository.reader()).asJsonObject

        json.keySet().forEach { moduleId ->
            val module = json.get(moduleId).asJsonObject
            val display = module.get("display").asString
            val fileName = module.get("file_name").asString

            val source = module.get("source").asJsonObject
            val sourceType = RemoteModuleSourceType.valueOf(source.get("type").asString)
            val sourceLocation = source.get("location").asString

            modules.add(RemoteModule(moduleId, display, fileName, RemoteModuleSource(sourceType, sourceLocation)))
        }
    }

    fun download(moduleId: String){
        val module = modules.firstOrNull { it.id == moduleId } ?: throw NullPointerException("Module with id '$moduleId' not found")
        val source = module.source
        val sourceType = source.type
        val sourceLocation = source.location
        val sourceFormat = source.format

        when(sourceType){
            RemoteModuleSourceType.GIT -> {
                // If it's from git we're using github's api to download the file
                val latestReleaseManifest = URL("https://api.github.com/repos/$sourceLocation/releases/latest").let {
                    JsonParser.parseReader(it.openStream().reader()).asJsonObject
                }

                val assets = latestReleaseManifest.get("assets").asJsonArray
                val asset = if (sourceFormat != null) {
                    val regexPattern = "^${sourceFormat.replace("*", "[^.]+")}.jar$"
                    assets.firstOrNull {
                        it.asJsonObject.get("name").asString.matches(regexPattern.toRegex())
                    } ?: throw NullPointerException("No asset found for module '$moduleId' with format '$sourceFormat'")
                } else {
                    assets.firstOrNull() ?: throw NullPointerException("No assets found for module '$moduleId'")
                }

            }
            RemoteModuleSourceType.URL -> {

            }
            else -> {
                throw NullPointerException("Unknown source type: $sourceType")
            }
        }
    }
}

data class RemoteModule(
    val id: String,
    val display: String,
    val fileName: String,
    val source: RemoteModuleSource,
)

data class RemoteModuleSource(
    val type: RemoteModuleSourceType,
    val location: String,
    val format: String? = null, // For example, if there's a file called "module-1.0.0-snapshot.jar" we can use the format "module-*-snapshot.jar" to get the latest version. If null, we'll use the file name as it is
)

enum class RemoteModuleSourceType(val id: String){
    GIT("git"),
    URL("url"),
}