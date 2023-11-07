package xyz.theprogramsrc.simplecoreapi.global.modules.filesmodule.config

import xyz.theprogramsrc.simplecoreapi.global.modules.filesmodule.extensions.file
import java.io.File
import java.util.*

/**
 * Representation of a [java.util.Properties] based config file
 * @param file the file to load the config from (usually a file ending with .properties)
 */
class PropertiesConfig(val file: File){

    val props = Properties()

    init {
        load()
    }

    /**
     * Loads the config from the file
     * @return This [PropertiesConfig]
     */
    fun load() = run {
        props.load(file.file().inputStream())
        this
    }

    /**
     * Saves the config to the file
     * @return This [PropertiesConfig]
     */
    fun save(vararg comments: String) = run {
        props.store(file.file().outputStream(), comments.joinToString("\n"))
        this
    }

    /**
     * Checks if the config contains the given key
     *
     * @param key the key to check
     */
    fun has(key: String) = props.containsKey(key)

    /**
     * Gets the value of the given key
     * @param key the key to get the value of
     * @return the value of the key if it exists, null otherwise
     */
    fun get(key: String): String? = if(has(key)) props.getProperty(key) else null

    /**
     * Gets the value of the given key, or
     * sets the given default value if the key doesn't exist
     *
     * @param key the key to get the value of
     * @param default the default value to return if the key doesn't exist
     * @return the value of the key if it exists, the default value otherwise
     */
    fun getOrDefault(key: String, default: String): String {
        add(key, default)
        return get(key) ?: default
    }

    /**
     * Sets the value of the given key
     * @param key the key to set the value of
     * @param value the value to set
     * @return This [PropertiesConfig]
     */
    fun set(key: String, value: String) = run {
        props.setProperty(key, value)
        save()
        this
    }

    /**
     * Adds the given key-value pair to the config
     * and if the key already exists, it won't be overwritten
     *
     * @param key the key to add
     * @param value the value to add
     * @return This [PropertiesConfig]
     */
    fun add(key: String, value: String) = run {
        if (!has(key)) {
            props.setProperty(key, value)
            save()
        }
        this
    }

    /**
     * Removes the given key
     * @param key the key to remove
     * @return This [PropertiesConfig]
     */
    fun remove(key: String) = run {
        props.remove(key)
        save()
        this
    }

    /**
     * Destroys the config
     * @return This [PropertiesConfig]
     */
    fun destroy() = run {
        if(file.exists()) file.delete()
        this
    }
}