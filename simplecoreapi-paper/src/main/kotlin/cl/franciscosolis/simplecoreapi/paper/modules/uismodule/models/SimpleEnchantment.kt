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

package cl.franciscosolis.simplecoreapi.paper.modules.uismodule.models

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import net.kyori.adventure.key.Key
import org.bukkit.enchantments.Enchantment

/**
 * Represents an enchantment
 * @param enchantment the enchantment
 * @param level the level of the enchantment
 */
data class SimpleEnchantment(val enchantment: Enchantment, val level: Int = 1) {

    /**
     * Gets the name of the enchantment
     * @return the name of the enchantment
     */
    val name: String
        get() = enchantment.key.key

    /**
     * Gets the level of the enchantment
     * @return the level of the enchantment
     */
    val enchantmentLevel: Int
        get() = level

    /**
     * Gets the enchantment as a string
     * @return the enchantment as a string
     */
    override fun toString(): String {
        val registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)
        return JsonObject().apply {
            addProperty("enchantment", registry.getKeyOrThrow(enchantment).toString())
            addProperty("level", level)
        }.toString()
    }

    companion object {
        /**
         * Parses a string to a [SimpleEnchantment]
         * @param string the string to parse
         * @return the parsed [SimpleEnchantment]
         */
        fun fromString(string: String): SimpleEnchantment {
            val json = JsonParser.parseString(string).asJsonObject
            val enchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).getOrThrow(Key.key(json.get("enchantment").asString))
            return SimpleEnchantment(enchantment, json.get("level").asInt)
        }
    }
}