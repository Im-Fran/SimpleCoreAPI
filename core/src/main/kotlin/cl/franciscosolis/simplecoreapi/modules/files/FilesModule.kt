package cl.franciscosolis.simplecoreapi.modules.files

import cl.franciscosolis.simplecoreapi.SimpleCoreAPI
import cl.franciscosolis.simplecoreapi.module.Module
import java.io.File

/**
 * Access to files and configurations inside the SimpleCoreAPI data folder.
 *
 * ```kotlin
 * val files = requireModule<FilesModule>()
 * val config = files.yaml("config.yml", mapOf("locale" to "en", "debug" to false))
 * ```
 *
 * Files are cached by path: asking twice for the same one returns the same instance, so two
 * plugins reading the same configuration see each other's changes.
 */
public class FilesModule : Module {

    @PublishedApi
    internal val cache: HashMap<String, DataFile> = HashMap()

    /** Current data folder. */
    public val dataFolder: File get() = SimpleCoreAPI.dataFolder

    /** Resolves [path] inside the data folder, creating the intermediate folders. */
    public fun file(path: String): File =
        File(dataFolder, path).also { it.parentFile?.mkdirs() }

    /** Opens (or creates) a YAML file and applies [defaults] to it. */
    public fun yaml(path: String, defaults: Map<String, Any?> = emptyMap()): YamlFile =
        cached(path, defaults) { YamlFile(file(path)) }

    /** Opens (or creates) a JSON file and applies [defaults] to it. */
    public fun json(path: String, defaults: Map<String, Any?> = emptyMap()): JsonFile =
        cached(path, defaults) { JsonFile(file(path)) }

    /** Clears the file cache. The next access reads from disk again. */
    public fun clearCache() {
        cache.clear()
    }

    private inline fun <reified T : DataFile> cached(
        path: String,
        defaults: Map<String, Any?>,
        create: () -> T,
    ): T {
        val data = cache.getOrPut(path, create)
        val typed = data as? T
            ?: error("'$path' was already opened as ${data::class.simpleName}, not as ${T::class.simpleName}")
        if (defaults.isNotEmpty()) typed.applyDefaults(defaults)
        return typed
    }

    override fun onDisable() {
        cache.clear()
    }
}
