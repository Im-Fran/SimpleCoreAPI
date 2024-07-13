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

import org.simpleyaml.configuration.ConfigurationSection
import org.simpleyaml.configuration.file.YamlConfiguration
import cl.franciscosolis.simplecoreapi.global.modules.filesmodule.extensions.file
import java.io.File

/**
 * Representation of a YAML config file
 *
 * @param file The file to load the config from (usually a file ending in .yml)
 */
class YmlConfig(val file: File){

    /**
     * The raw [YamlConfiguration] file
     */
    var config = YamlConfiguration.loadConfiguration(file.file())

    init {
        load()
    }

    /**
     * Loads the config file and clears the current cache
     * @return This [YmlConfig]
     */
    fun load() = run {
        config = YamlConfiguration.loadConfiguration(file.file())
        this
    }

    /**
     * Saves the config file
     * @return This [YmlConfig]
     */
    fun save() = run {
        config.save(file.file())
        this
    }

    /**
     * Destroys the config file
     * @return This [YmlConfig]
     */
    fun destroy() = run {
        if (file.exists()) file.delete()
        this
    }

    /**
     * Checks if the given path exists
     *
     * @param path The path to check
     */
    fun has(path: String): Boolean = config.contains(path)

    /**
     * Sets the value of the given path
     *
     * @param path The path to set the value of
     * @param value The value to set
     * @return This [YmlConfig]
     */
    fun set(path: String, value: Any) = run {
        config.set(path, value)
        save()
        this
    }

    /**
     * Adds the value of the given path without
     * replacing it if it already exists
     *
     * @param path The path to set the value of
     * @param value The value to set
     * @return This [YmlConfig]
     */
    fun add(path: String, value: Any) = run {
        if(!has(path)){
            set(path, value)
        }
        this
    }

    /**
     * Removes the value of the given path
     * @param path The path to remove the value of
     * @return This [YmlConfig]
     */
    fun remove(path: String) = run {
        config.remove(path)
        save()
        this
    }

    /**
     * Gets the value of the given path
     *
     * @param path The path to get the value of
     * @return The value of the given path as [Any]
     */
    fun get(path: String): Any = config.get(path)

    /**
     * Gets the value of the given path
     * or adds the default value if it doesn't exist
     *
     * @param path The path to get the value of
     * @param default The default value to set if the path doesn't exist
     * @return The value of the given path as [Any]
     */
    fun getOrAdd(path: String, default: Any): Any = if(has(path)) get(path) else set(path, default).get(path)

    /**
     * Gets the value of the given path as a [String]
     *
     * @param path The path to get the value of
     * @return The value of the given path as [String]
     */
    fun getString(path: String): String = config.getString(path)

    /**
     * Gets the value of the given path as a [String]
     * or adds the default value if it doesn't exist
     *
     * @param path The path to get the value of
     * @param default The default value to set if the path doesn't exist
     * @return The value of the given path as [String]
     */
    fun getStringOrAdd(path: String, default: String): String = if(has(path)) getString(path) else set(path, default).getString(path)

    /**
     * Gets the value of the given path as a [Int]
     *
     * @param path The path to get the value of
     * @return The value of the given path as [Int]
     */
    fun getInt(path: String): Int = config.getInt(path)

    /**
     * Gets the value of the given path as a [Int]
     * or adds the default value if it doesn't exist
     *
     * @param path The path to get the value of
     * @param default The default value to set if the path doesn't exist
     * @return The value of the given path as [Int]
     */
    fun getIntOrAdd(path: String, default: Int): Int = if(has(path)) getInt(path) else set(path, default).getInt(path)

    /**
     * Gets the value of the given path as a [Double]
     *
     * @param path The path to get the value of
     * @return The value of the given path as [Double]
     */
    fun getDouble(path: String): Double = config.getDouble(path)

    /**
     * Gets the value of the given path as a [Double]
     * or adds the default value if it doesn't exist
     *
     * @param path The path to get the value of
     * @param default The default value to set if the path doesn't exist
     * @return The value of the given path as [Double]
     */
    fun getDoubleOrAdd(path: String, default: Double): Double = if(has(path)) getDouble(path) else set(path, default).getDouble(path)

    /**
     * Gets the value of the given path as a [Float]
     *
     * @param path The path to get the value of
     * @return The value of the given path as [Float]
     */
    fun getFloat(path: String): Float = config.get(path) as Float

    /**
     * Gets the value of the given path as a [Float]
     * or adds the default value if it doesn't exist
     *
     * @param path The path to get the value of
     * @param default The default value to set if the path doesn't exist
     * @return The value of the given path as [Float]
     */
    fun getFloatOrAdd(path: String, default: Float): Float = if(has(path)) getFloat(path) else set(path, default).getFloat(path)

    /**
     * Gets the value of the given path as a [Long]
     *
     * @param path The path to get the value of
     * @return The value of the given path as [Long]
     */
    fun getLong(path: String): Long = config.getLong(path)

    /**
     * Gets the value of the given path as a [Long]
     * or adds the default value if it doesn't exist
     *
     * @param path The path to get the value of
     * @param default The default value to set if the path doesn't exist
     * @return The value of the given path as [Long]
     */
    fun getLongOrAdd(path: String, default: Long): Long = if(has(path)) getLong(path) else set(path, default).getLong(path)

    /**
     * Gets the value of the given path as a [Boolean]
     *
     * @param path The path to get the value of
     * @return The value of the given path as [Boolean]
     */
    fun getBoolean(path: String): Boolean = config.getBoolean(path)

    /**
     * Gets the value of the given path as a [Boolean]
     * or adds the default value if it doesn't exist
     *
     * @param path The path to get the value of
     * @param default The default value to set if the path doesn't exist
     * @return The value of the given path as [Boolean]
     */
    fun getBooleanOrAdd(path: String, default: Boolean): Boolean = if(has(path)) getBoolean(path) else set(path, default).getBoolean(path)

    /**
     * Gets a [List] of [String] from the given path
     *
     * @param path The path to get the value of
     * @return The value of the given path as [List] of [String]
     */
    fun getStringList(path: String): List<String> = config.getStringList(path)

    /**
     * Gets a [List] of [String] from the given path
     * or adds the default value if it doesn't exist
     *
     * @param path The path to get the value of
     * @param default The default value to set if the path doesn't exist
     * @return The value of the given path as [List] of [String]
     */
    fun getStringListOrAdd(path: String, default: List<String>): List<String> = if(has(path)) getStringList(path) else set(path, default).getStringList(path)

    /**
     * Gets a [List] of [Int] from the given path
     *
     * @param path The path to get the value of
     * @return The value of the given path as [List] of [Int]
     */
    fun getIntList(path: String): List<Int> = config.getIntegerList(path)

    /**
     * Gets a [List] of [Int] from the given path
     * or adds the default value if it doesn't exist
     *
     * @param path The path to get the value of
     * @param default The default value to set if the path doesn't exist
     * @return The value of the given path as [List] of [Int]
     */
    fun getIntListOrAdd(path: String, default: List<Int>): List<Int> = if(has(path)) getIntList(path) else set(path, default).getIntList(path)

    /**
     * Gets a [List] of [Double] from the given path
     *
     * @param path The path to get the value of
     * @return The value of the given path as [List] of [Double]
     */
    fun getDoubleList(path: String): List<Double> = config.getDoubleList(path)

    /**
     * Gets a [List] of [Double] from the given path
     * or adds the default value if it doesn't exist
     *
     * @param path The path to get the value of
     * @param default The default value to set if the path doesn't exist
     * @return The value of the given path as [List] of [Double]
     */
    fun getDoubleListOrAdd(path: String, default: List<Double>): List<Double> = if(has(path)) getDoubleList(path) else set(path, default).getDoubleList(path)

    /**
     * Gets a [List] of [Float] from the given path
     *
     * @param path The path to get the value of
     * @return The value of the given path as [List] of [Float]
     */
    fun getFloatList(path: String): List<Float> = config.getFloatList(path)

    /**
     * Gets a [List] of [Float] from the given path
     * or adds the default value if it doesn't exist
     *
     * @param path The path to get the value of
     * @param default The default value to set if the path doesn't exist
     * @return The value of the given path as [List] of [Float]
     */
    fun getFloatListOrAdd(path: String, default: List<Float>): List<Float> = if(has(path)) getFloatList(path) else set(path, default).getFloatList(path)

    /**
     * Gets a [List] of [Long] from the given path
     *
     * @param path The path to get the value of
     * @return The value of the given path as [List] of [Long]
     */
    fun getLongList(path: String): List<Long> = config.getLongList(path)

    /**
     * Gets a [List] of [Long] from the given path
     * or adds the default value if it doesn't exist
     *
     * @param path The path to get the value of
     * @param default The default value to set if the path doesn't exist
     * @return The value of the given path as [List] of [Long]
     */
    fun getLongListOrAdd(path: String, default: List<Long>): List<Long> = if(has(path)) getLongList(path) else set(path, default).getLongList(path)

    /**
     * Gets a [List] of [Boolean] from the given path
     *
     * @param path The path to get the value of
     * @return The value of the given path as [List] of [Boolean]
     */
    fun getBooleanList(path: String): List<Boolean> = config.getBooleanList(path)

    /**
     * Gets a [List] of [Boolean] from the given path
     * or adds the default value if it doesn't exist
     *
     * @param path The path to get the value of
     * @param default The default value to set if the path doesn't exist
     * @return The value of the given path as [List] of [Boolean]
     */
    fun getBooleanListOrAdd(path: String, default: List<Boolean>): List<Boolean> = if(has(path)) getBooleanList(path) else set(path, default).getBooleanList(path)

    /**
     * Gets a [List] from the given path
     *
     * @param path The path to get the value of
     * @return The value of the given path as [List]
     */
    fun getList(path: String): List<*> = config.getList(path)

    /**
     * Gets a [List] from the given path
     * or adds the default value if it doesn't exist
     *
     * @param path The path to get the value of
     * @param default The default value to set if the path doesn't exist
     * @return The value of the given path as [List]
     */
    fun getListOrAdd(path: String, default: List<*>): List<*> = if(has(path)) getList(path) else set(path, default).getList(path)

    /**
     * Gets a [Map] from the given path
     *
     * @param path The path to get the value of
     * @return The value of the given path as [Map]
     */
    fun getMap(path: String): Map<String, Any> = config.getConfigurationSection(path).let {
        it.getKeys(true).associateWith { key -> it.get(key) }
    }

    /**
     * Gets a [Map] from the given path
     * or adds the default value if it doesn't exist
     *
     * @param path The path to get the value of
     * @param default The default value to set if the path doesn't exist
     * @return The value of the given path as [Map]
     */
    fun getMapOrAdd(path: String, default: Map<String, Any>): Map<String, Any> = if(has(path)) getMap(path) else default.onEach { (key, value) -> set("$path.$key", value) }

    /**
     * Gets the entry set of the [YmlConfig]
     *
     * @param deep If true, the keys will contain all the keys within any child node (and their children, recursively). Otherwise, this will contain only the keys of any direct children, and not their own children.
     * @return The entry set of the [YmlConfig]
     */
    fun entries(deep: Boolean = false): Set<Map.Entry<String, Any>> = config.getKeys(deep).associateWith { get(it) }.entries

    /**
     * Gets the keys of the [YmlConfig]
     *
     * @param deep If true, the keys will contain all the keys within any child node (and their children, recursively). Otherwise, this will contain only the keys of any direct children, and not their own children.
     * @return The keys of the [YmlConfig]
     */
    fun keys(deep: Boolean = false): Set<String> = config.getKeys(deep)

    /**
     * Gets a configuration section of the given path
     *
     * @param path The path to get the section of
     * @return The configuration section of the given path
     */
    fun getSection(path: String): ConfigurationSection = config.getConfigurationSection(path)

}