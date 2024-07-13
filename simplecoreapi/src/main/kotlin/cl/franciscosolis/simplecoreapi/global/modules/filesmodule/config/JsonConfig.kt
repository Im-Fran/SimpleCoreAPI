/*
 * SimpleCoreAPI - Kotlin Project Library
 * Copyright (C) 2024 Francisco Solís
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package cl.franciscosolis.simplecoreapi.global.modules.filesmodule.config

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import cl.franciscosolis.simplecoreapi.global.modules.filesmodule.extensions.file
import java.io.File

/**
 * Representation of a json config
 */
class JsonConfig {

    var file: File? = null
    var jsonObject = JsonObject()

    /**
     * Loads the config from the given object
     * (but not saved to a file)
     *
     * @param jsonObject the json object
     */
    constructor(jsonObject: JsonObject){
        this.jsonObject = jsonObject
    }

    /**
     * Loads the config from the given file
     *
     * @param file the file
     */
    constructor(file: File){
        this.file = file
        load()
    }

    /**
     * Loads the config from the file, and overrides the cache
     */
    fun load() = run {
        if(file != null){
            val file = file!!.file()
            val text = file.readText()
            if(file.length() > 1000000000L) {
                throw Exception("File is too big to be loaded!")
            }
            jsonObject = if(text.isBlank()) JsonObject() else JsonParser.parseString(text).asJsonObject
        }
        this
    }

    /**
     * Saves the config to the file
     */
    fun save() = run {
        if(file != null){
            file?.file()?.writeText(jsonObject.toString())
        }
        this
    }

    /**
     * Destroys the config
     */
    fun destroy() = run {
        if(file?.exists() == true) file?.delete()
        this
    }

    /**
     * Checks if the config has the specified key
     *
     * @param key The key to check
     */
    fun has(key: String) = jsonObject.has(key)

    /**
     * Sets a [JsonConfig] to the given key, and
     * if the member already exists, it will be replaced
     *
     * @param key The key to set
     * @param value The [JsonConfig]
     */
    fun set(key: String, value: JsonConfig) = run {
        jsonObject.add(key, value.jsonObject)
        this
    }

    /**
     * Adds a [JsonConfig] to the given key, and
     * if the member already exists, it won't be replaced
     *
     * @param key The key to add
     * @param value The [JsonConfig]
     */
    fun add(key: String, value: JsonConfig) = run {
        if(!has(key)) {
            set(key, value)
        }
        this
    }

    /**
     * Sets a [JsonElement] to the given key, and
     * if the member already exists, it will be replaced.
     *
     * @param key The key of the element
     * @param element The [JsonElement]
     */
    fun set(key: String, element: JsonElement) = run {
        jsonObject.add(key, element)
        save()
        this
    }

    /**
     * Adds an element to the given key, and
     * if the member already exists, it won't be replaced.
     *
     * @param key The key of the element
     * @param element The element
     */
    fun add(key: String, element: JsonElement) = run {
        if (!has(key)) {
            set(key, element)
        }
        this
    }

    /**
     * Gets an element from the given key
     *
     * @param key The key of the element
     * @return The element
     */
    fun get(key: String) = jsonObject.get(key)

    /**
     * Sets a [String] to the given key, and
     * if the member already exists, it will be replaced.
     *
     * @param key The key of the [String]
     * @param value The [String] value
     */
    fun set(key: String, value: String) = run {
        jsonObject.addProperty(key, value)
        save()
        this
    }

    /**
     * Adds a [String] to the given key, and
     * if the member already exists, it won't be replaced.
     *
     * @param key The key of the [String]
     * @param value The [String] value
     */
    fun add(key: String, value: String) = run {
        if (!has(key)) {
            set(key, value)
        }
        this
    }

    /**
     * Gets a [String] from the given key
     *
     * @param key The key of the [String]
     * @return The [String] value
     */
    fun getString(key: String) = jsonObject.get(key).asString

    /**
     * Sets an [Int] to the given key, and
     * if the member already exists, it will be replaced.
     *
     * @param key The key of the [Int]
     * @param value The [Int] value
     */
    fun set(key: String, value: Int) = run {
        jsonObject.addProperty(key, value)
        save()
        this
    }

    /**
     * Adds an [Int] to the given key, and
     * if the member already exists, it won't be replaced.
     *
     * @param key The key of the [Int]
     * @param value The [Int] value
     */
    fun add(key: String, value: Int) = run {
        if (!has(key)) {
            set(key, value)
        }
        this
    }

    /**
     * Gets an [Int] from the given key
     *
     * @param key The key of the [Int]
     * @return The [Int] value
     */
    fun getInt(key: String) = jsonObject.get(key).asInt

    /**
     * Increases an [Int] by the given value
     * or by 1 if no value is specified.
     *
     * If the member doesn't exist, it will be set to 0.
     *
     * @param key The key of the [Int]
     * @param value The value to increase by. Defaults to 1
     * @return The new value
     */
    fun increase(key: String, value: Int = 1): Int {
        if (!has(key)) {
            set(key, 0)
        }else {
            set(key, getInt(key) + value)
        }
        return getInt(key)
    }

    /**
     * Decreases an [Int] by the given value
     * or by 1 if no value is specified.
     *
     * If the member doesn't exist, it will be set to 0.
     *
     * @param key The key of the [Int]
     * @param value The value to decrease by. Defaults to 1
     * @return The new value
     */
    fun decrease(key: String, value: Int = 1): Int {
        if (!has(key)) {
            set(key, 0)
        } else {
            set(key, getInt(key) - value)
        }
        return getInt(key)
    }

    /**
     * Sets a [Number] to the given key, and
     * if the member already exists, it will be replaced.
     *
     * @param key The key of the [Number]
     * @param value The [Number] value
     */
    fun set(key: String, value: Number) = run {
        jsonObject.addProperty(key, value)
        save()
        this
    }

    /**
     * Adds a [Number] to the given key, and
     * if the member already exists, it won't be replaced.
     *
     * @param key The key of the [Number]
     * @param value The [Number] value
     */
    fun add(key: String, value: Number) = run {
        if (!has(key)) {
            set(key, value)
        }
        this
    }

    /**
     * Gets a [Number] from the given key
     *
     * @param key The key of the [Number]
     * @return The [Number] value
     */
    fun getNumber(key: String) = jsonObject.get(key).asNumber

    /**
     * Sets a [Boolean] to the given key, and
     * if the member already exists, it will be replaced.
     *
     * @param key The key of the [Boolean]
     * @param value The [Boolean] value
     */
    fun set(key: String, value: Boolean) = run {
        jsonObject.addProperty(key, value)
        save()
        this
    }

    /**
     * Adds a [Boolean] to the given key, and
     * if the member already exists, it won't be replaced.
     *
     * @param key The key of the [Boolean]
     * @param value The [Boolean] value
     */
    fun add(key: String, value: Boolean) = run {
        if (!has(key)) {
            set(key, value)
        }
        this
    }

    /**
     * Gets a [Boolean] from the given key
     *
     * @param key The key of the [Boolean]
     * @return The [Boolean] value
     */
    fun getBoolean(key: String) = jsonObject.get(key).asBoolean

    /**
     * Toggles a [Boolean] to the given key, and
     * if the member doesn't exist, it will be set to true.
     *
     * @param key The key of the [Boolean]
     * @return The new [Boolean] value
     */
    fun toggle(key: String): Boolean {
        if (!has(key)) {
            set(key, true)
            return true
        }
        val value = getBoolean(key)
        set(key, !value)
        return !value
    }


    /**
     * Sets a [JsonArray] to the given key, and
     * if the member already exists, it will be replaced.
     *
     * @param key The key of the [JsonArray]
     * @param value The [JsonArray] value
     */
    fun set(key: String, value: JsonArray) = run {
        jsonObject.add(key, value)
        save()
        this
    }

    /**
     * Adds a [JsonArray] to the given key, and
     * if the member already exists, it won't be replaced.
     *
     * @param key The key of the [JsonArray]
     * @param value The [JsonArray] value
     */
    fun add(key: String, value: JsonArray) = run {
        if (!has(key)) {
            set(key, value)
        }
        this
    }

    /**
     * Gets a [JsonArray] from the given key
     *
     * @param key The key of the [JsonArray]
     * @return The [JsonArray] value
     */
    fun getJsonArray(key: String) = jsonObject.get(key).asJsonArray

    /**
     * Gets a [JsonArray] from the given key
     * or creates a new one if it doesn't exist
     *
     * @param key The key of the [JsonArray]
     * @return The [JsonArray] value
     */
    fun getOrCreateJsonArray(key: String): JsonArray {
        if (!has(key)) {
            set(key, JsonArray())
        }
        return getJsonArray(key)
    }

    /**
     * Sets a [JsonObject] to the given key, and
     * if the member already exists, it will be replaced.
     *
     * @param key The key of the [JsonObject]
     * @param value The [JsonObject] value
     */
    fun set(key: String, value: JsonObject) = run {
        jsonObject.add(key, value)
        save()
        this
    }

    /**
     * Adds a [JsonObject] to the given key, and
     * if the member already exists, it won't be replaced.
     *
     * @param key The key of the [JsonObject]
     * @param value The [JsonObject] value
     */
    fun add(key: String, value: JsonObject) = run {
        if (!has(key)) {
            set(key, value)
        }
        this
    }

    /**
     * Gets a [JsonConfig] from the given key
     *
     * @param key The key of the [JsonConfig]
     * @return The [JsonConfig] value
     */
    fun getJsonObject(key: String): JsonConfig = JsonConfig(jsonObject.get(key).asJsonObject)

    /**
     * Gets a [JsonConfig] from the given key
     * or creates a new one if it doesn't exist
     *
     * @param key The key of the [JsonConfig]
     * @return The [JsonConfig] value
     */
    fun getOrCreateJsonObject(key: String): JsonConfig {
        if (!has(key)) {
            set(key, JsonObject())
        }
        return getJsonObject(key)
    }

    /**
     * Removes the given key from the [JsonObject]
     *
     * @param key The key of the member
     * @return The [JsonElement] that is being removed
     */
    fun remove(key: String): JsonElement? {
        val result = jsonObject.remove(key)
        save()
        return result
    }
}