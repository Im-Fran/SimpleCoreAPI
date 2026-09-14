package cl.franciscosolis.simplecoreapi.modules.files

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File

/**
 * A JSON file accessed through dot-separated paths.
 *
 * Numbers arrive as `Double`, which is how gson hands them back; use [DataFile]'s
 * `getInt`/`getLong` instead of casting by hand.
 */
public class JsonFile(file: File) : DataFile(file) {

    init {
        reload()
    }

    override fun deserialize(text: String): MutableMap<String, Any?> =
        GSON.fromJson<MutableMap<String, Any?>>(text, MAP_TYPE) ?: LinkedHashMap()

    override fun serialize(data: Map<String, Any?>): String = GSON.toJson(data)

    private companion object {
        private val GSON = GsonBuilder().setPrettyPrinting().serializeNulls().create()
        private val MAP_TYPE = object : TypeToken<LinkedHashMap<String, Any?>>() {}.type
    }
}
