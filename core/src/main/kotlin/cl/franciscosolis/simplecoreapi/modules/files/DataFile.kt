package cl.franciscosolis.simplecoreapi.modules.files

import java.io.File

/**
 * A data file backed by a nested map, accessed through dot-separated paths.
 *
 * Shared base of [YamlFile] and [JsonFile]: all the navigation and typed access lives here,
 * and subclasses only supply how text becomes a map and back.
 *
 * Changes stay in memory until [save] is called.
 *
 * The dot is always a path separator: a key containing dots cannot be reached directly,
 * only through [section].
 */
public abstract class DataFile(public val file: File) {

    private var data: MutableMap<String, Any?> = LinkedHashMap()

    /** Full read-only contents of the file. */
    public val root: Map<String, Any?> get() = data

    protected abstract fun deserialize(text: String): MutableMap<String, Any?>

    protected abstract fun serialize(data: Map<String, Any?>): String

    /** Re-reads the file from disk, discarding unsaved changes. */
    public fun reload() {
        data = if (file.isFile) {
            val text = file.readText()
            if (text.isBlank()) LinkedHashMap() else deserialize(text)
        } else {
            LinkedHashMap()
        }
    }

    /** Writes the current contents to disk, creating any missing folders. */
    public fun save() {
        file.parentFile?.mkdirs()
        file.writeText(serialize(data))
    }

    /**
     * Fills in the missing keys from [defaults] and saves if anything changed.
     *
     * This is the usual configuration pattern: new keys show up in the user's file without
     * overwriting the values they already customized.
     *
     * @return `true` if something was written.
     */
    public fun applyDefaults(defaults: Map<String, Any?>): Boolean {
        val changed = defaults.entries.count { (path, value) -> add(path, value) } > 0
        if (changed) save()
        return changed
    }

    /** Sets [value] only if [path] does not exist. @return `true` if it was set. */
    public fun add(path: String, value: Any?): Boolean {
        if (contains(path)) return false
        set(path, value)
        return true
    }

    public operator fun get(path: String): Any? {
        val (parent, key) = resolve(path, create = false) ?: return null
        return parent[key]
    }

    public operator fun set(path: String, value: Any?) {
        val (parent, key) = resolve(path, create = true)!!
        parent[key] = value
    }

    public operator fun contains(path: String): Boolean {
        val (parent, key) = resolve(path, create = false) ?: return false
        return parent.containsKey(key)
    }

    /** Removes [path]. @return `true` if it existed. */
    public fun remove(path: String): Boolean {
        val (parent, key) = resolve(path, create = false) ?: return false
        if (!parent.containsKey(key)) return false
        parent.remove(key)
        return true
    }

    /** Direct keys under [path], or the root ones when it is `null`. */
    public fun keys(path: String? = null): Set<String> =
        if (path == null) data.keys.toSet() else section(path)?.keys.orEmpty()

    /** Sub-map at [path], or `null` if it does not exist or is not a map. */
    @Suppress("UNCHECKED_CAST")
    public fun section(path: String): Map<String, Any?>? = get(path) as? Map<String, Any?>

    public fun getString(path: String, default: String? = null): String? =
        get(path)?.toString() ?: default

    public fun getInt(path: String, default: Int = 0): Int = number(path)?.toInt() ?: default

    public fun getLong(path: String, default: Long = 0L): Long = number(path)?.toLong() ?: default

    public fun getDouble(path: String, default: Double = 0.0): Double =
        number(path)?.toDouble() ?: default

    public fun getBoolean(path: String, default: Boolean = false): Boolean = when (val v = get(path)) {
        is Boolean -> v
        is String -> v.toBooleanStrictOrNull() ?: default
        else -> default
    }

    public fun getList(path: String, default: List<Any?> = emptyList()): List<Any?> =
        get(path) as? List<Any?> ?: default

    public fun getStringList(path: String, default: List<String> = emptyList()): List<String> =
        (get(path) as? List<*>)?.map { it.toString() } ?: default

    /** Accepts both real numbers and numeric strings, since json hands back doubles. */
    private fun number(path: String): Number? = when (val v = get(path)) {
        is Number -> v
        is String -> v.toDoubleOrNull()
        else -> null
    }

    /**
     * Locates the map holding the last segment of [path].
     *
     * With `create = false` it returns `null` when an intermediate segment is missing or is
     * not a map; with `create = true` it creates the intermediate maps as needed.
     */
    @Suppress("UNCHECKED_CAST")
    private fun resolve(path: String, create: Boolean): Pair<MutableMap<String, Any?>, String>? {
        require(path.isNotEmpty()) { "Path cannot be empty" }
        val parts = path.split('.')
        var current = data
        for (part in parts.dropLast(1)) {
            val next = current[part]
            current = when {
                next is MutableMap<*, *> -> next as MutableMap<String, Any?>
                next is Map<*, *> -> LinkedHashMap(next as Map<String, Any?>)
                    .also { current[part] = it }
                create -> LinkedHashMap<String, Any?>().also { current[part] = it }
                else -> return null
            }
        }
        return current to parts.last()
    }
}
