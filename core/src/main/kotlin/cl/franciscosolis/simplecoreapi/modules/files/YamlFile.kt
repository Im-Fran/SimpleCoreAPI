package cl.franciscosolis.simplecoreapi.modules.files

import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.File

/**
 * A YAML file accessed through dot-separated paths.
 *
 * ```kotlin
 * val config = requireModule<FilesModule>().yaml("config.yml", mapOf("debug" to false))
 * if (config.getBoolean("debug")) { /* ... */ }
 * ```
 */
public class YamlFile(file: File) : DataFile(file) {

    init {
        reload()
    }

    @Suppress("UNCHECKED_CAST")
    override fun deserialize(text: String): MutableMap<String, Any?> =
        (Yaml().load<Any?>(text) as? Map<String, Any?>)
            ?.let { LinkedHashMap(it) }
            ?: LinkedHashMap()

    override fun serialize(data: Map<String, Any?>): String = Yaml(
        DumperOptions().apply {
            defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
            isPrettyFlow = true
            indent = 2
        },
    ).dump(data)
}
