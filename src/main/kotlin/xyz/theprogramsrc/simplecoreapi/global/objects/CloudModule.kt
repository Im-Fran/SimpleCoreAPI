package xyz.theprogramsrc.simplecoreapi.global.objects

import com.google.gson.JsonParser
import java.net.URL

data class CloudModule(val id: Int, val name: String)

class CloudModuleStorage {

    companion object {
        val instance = CloudModuleStorage()
    }

    private val cache = mutableMapOf<String, CloudModule>()

    fun loadAll(){
        val localCache = mutableMapOf<String, CloudModule>()
        JsonParser().parse(URL("https://theprogramsrc.xyz/api/v1/products/product-tag/4").readText()).asJsonObject.get("data").asJsonArray.forEach { el ->
            val json = el.asJsonObject
            localCache[json.get("name").asString] = CloudModule(json.get("id").asInt, json.get("name").asString)
        }
        cache.clear()
        cache.putAll(localCache)
    }

    fun get(name: String): CloudModule? = if(cache[name] != null){
        cache[name]
    } else {
        loadAll()
        cache[name]
    }

}