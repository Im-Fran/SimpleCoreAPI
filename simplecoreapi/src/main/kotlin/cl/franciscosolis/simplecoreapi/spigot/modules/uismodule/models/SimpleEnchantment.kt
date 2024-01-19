package cl.franciscosolis.simplecoreapi.spigot.modules.uismodule.models

import org.bukkit.enchantments.Enchantment

/**
 * Represents an enchantment
 * @param enchantment the enchantment
 * @param level the level of the enchantment
 */
data class SimpleEnchantment(val enchantment: Enchantment, val level: Int = 1)