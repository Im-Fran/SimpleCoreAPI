package xyz.theprogramsrc.simplecoreapi.global.module

import com.google.gson.JsonParser
import xyz.theprogramsrc.simplecoreapi.global.objects.CloudModuleStorage
import java.io.File
import java.net.URL

/**
 * Module Helper to Download or Sort modules
 */
object ModuleHelper {
    private val downloadLocation = File("plugins/SimpleCoreAPI/modules/")

    /**
     * Downloads a Module from the database
     * @param name Module Name
     * @return true if the module was downloaded, false otherwise
     */
    fun downloadModule(name: String): Boolean{
        if(!downloadLocation.exists()) downloadLocation.mkdirs()
        val module = CloudModuleStorage.instance.get(name) ?: return false
        val data = JsonParser().parse(URL("https://theprogramsrc.xyz/api/v1/products/id/${module.id}").readText()).asJsonObject.get("data").asJsonObject.get("versions").asJsonArray.firstOrNull() ?: return false
        return data.asJsonObject.get("download_url").asString.let {
            URL(it).openStream().use { inputStream ->
                File(downloadLocation, "$name.jar").outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                    true
                }
            }
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
        return sorted
    }
}