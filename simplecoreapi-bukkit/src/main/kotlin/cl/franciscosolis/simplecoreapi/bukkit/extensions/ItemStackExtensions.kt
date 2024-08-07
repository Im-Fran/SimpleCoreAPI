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

package cl.franciscosolis.simplecoreapi.bukkit.extensions

import cl.franciscosolis.simplecoreapi.bukkit.modules.uismodule.models.SimpleEnchantment
import com.cryptomorin.xseries.XMaterial
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta

/* ItemStack Extensions */

/**
 * Gets the name of this [ItemStack] (if it has [ItemMeta])
 * @return the name of the item
 */
val ItemStack.name: String?
    get() = this.itemMeta?.displayName

/**
 * Sets the name of this [ItemStack]
 * @param name the name of the item
 * @return this [ItemStack]
 */
fun ItemStack.name(name: String): ItemStack = this.apply {
    this.itemMeta = this.itemMeta?.apply {
        setDisplayName(name.bukkitColor())
    }
}

/**
 * Gets the lore of this [ItemStack]
 * @return the lore of the item
 */
val ItemStack.lore: List<String>
    get() = this.itemMeta?.lore ?: emptyList()

/**
 * Sets the lore of this [ItemStack]
 * @param lore the lore of the item
 * @param append if the lore should be appended or replaced
 * @return this [ItemStack]
 */
fun ItemStack.lore(lore: List<String>, append: Boolean = false): ItemStack = this.apply {
    this.itemMeta = this.itemMeta?.apply {
        val colored = lore.map { it.bukkitColor() }
        this.lore = if(append){
            (this.lore ?: emptyList()).plus(colored)
        } else {
            colored
        }
    }
}

/**
 * Sets the lore of this [ItemStack]
 * @param lore the lore of the item
 * @param append if the lore should be appended or replaced
 * @return this [ItemStack]
 */
fun ItemStack.lore(vararg lore: String, append: Boolean = false): ItemStack = this.lore(lore.toList(), append)

/**
 * Sets the amount of this [ItemStack]
 * @param amount the amount of the item
 * @return this [ItemStack]
 */
fun ItemStack.amount(amount: Int): ItemStack = this.apply {
    this.amount = amount
}

/**
 * Gets the flags of this [ItemStack]
 * @return the flags of the item
 */
val ItemStack.flags: List<ItemFlag>
    get() = this.itemMeta?.itemFlags?.toList() ?: emptyList()

/**
 * Toggles the given flags on this [ItemStack]. If the flag is already set, it will be removed.
 * @param flags flags to toggle
 * @return this [ItemStack]
 */
fun ItemStack.toggleFlags(vararg flags: ItemFlag): ItemStack = this.apply {
    this.itemMeta = this.itemMeta?.apply {
        flags.forEach {
            if(this.hasItemFlag(it)){
                this.removeItemFlags(it)
            } else {
                this.addItemFlags(it)
            }
        }
    }
}

/**
 * Adds the given flags to this [ItemStack]
 * @param flags the flags to add
 * @return this [ItemStack]
 */
fun ItemStack.addFlags(vararg flags: ItemFlag): ItemStack = this.apply {
    this.itemMeta = this.itemMeta?.apply {
        addItemFlags(*flags)
    }
}

/**
 * Removes the given flags from this [ItemStack]
 * @param flags the flags to remove
 * @return this [ItemStack]
 */
fun ItemStack.removeFlags(vararg flags: ItemFlag): ItemStack = this.apply {
    this.itemMeta = this.itemMeta?.apply {
        removeItemFlags(*flags)
    }
}

/**
 * Toggles the given enchantments on this [ItemStack]. If the enchantment is already set, it will be removed.
 * @param enchantments the enchantments to toggle
 * @return this [ItemStack]
 */
fun ItemStack.toggleEnchantments(vararg enchantments: SimpleEnchantment): ItemStack = this.apply {
    this.itemMeta = this.itemMeta?.apply {
        enchantments.forEach {
            if (this.hasEnchant(it.enchantment)) {
                this.removeEnchant(it.enchantment)
            } else {
                this.addEnchant(it.enchantment, it.level, true)
            }
        }
    }
}

/**
 * Adds the given enchantments to this [ItemStack]
 * @param enchantments the enchantments to add
 * @return this [ItemStack]
 */
fun ItemStack.addEnchantments(vararg enchantments: SimpleEnchantment): ItemStack = this.apply {
    this.itemMeta = this.itemMeta?.apply {
        enchantments.forEach {
            this.addEnchant(it.enchantment, it.level, true)
        }
    }
}

/**
 * Adds the given enchantments to this [ItemStack] with default level 1
 * @param enchantments the enchantments to add
 * @return this [ItemStack]
 */
fun ItemStack.addEnchantments(vararg enchantments: Enchantment): ItemStack = this.apply {
    this.addEnchantments(*enchantments.map { SimpleEnchantment(it) }.toTypedArray())
}

/**
 * Removes the given enchantments from this [ItemStack]
 * @param enchantments the enchantments to remove
 * @return this [ItemStack]
 */
fun ItemStack.removeEnchantments(vararg enchantments: SimpleEnchantment): ItemStack = this.apply {
    this.itemMeta = this.itemMeta?.apply {
        enchantments.forEach {
            this.removeEnchant(it.enchantment)
        }
    }
}

/**
 * Removes the given enchantments from this [ItemStack]
 * @param enchantments the enchantments to remove
 * @return this [ItemStack]
 */
fun ItemStack.removeEnchantments(vararg enchantments: Enchantment): ItemStack = this.apply {
    this.removeEnchantments(*enchantments.map { SimpleEnchantment(it) }.toTypedArray())
}

/**
 * Sets the glowing effect on this [ItemStack]. (If true the enchantments will be hidden)
 * @param glowing if the item should glow. (Defaults to true)
 * @return this [ItemStack]
 */
fun ItemStack.setGlowing(glowing: Boolean = true): ItemStack = this.apply {
    this.itemMeta = this.itemMeta?.apply {
        setEnchantmentGlintOverride(glowing)
    }
}

/**
 * Sets the damage to this [ItemStack]
 * @param damage the damage to the item
 * @return this [ItemStack]
 */
fun ItemStack.damage(damage: Int): ItemStack = this.apply {
    val meta = this.itemMeta ?: return this
    if(meta is Damageable) {
        meta.damage = damage
        this.itemMeta = meta
    }
}

/**
 * Sets the color if the item is leather armor
 * @param color the color of the item
 */
fun ItemStack.color(color: Color): ItemStack = this.apply {
    val meta = this.itemMeta ?: return this
    if ((type == Material.LEATHER_BOOTS || type == Material.LEATHER_CHESTPLATE || type == Material.LEATHER_HELMET || type == Material.LEATHER_LEGGINGS) && meta is LeatherArmorMeta) {
        meta.setColor(color)
        this.itemMeta = meta
    } else {
        throw IllegalArgumentException("Colors only applicable for leather armor!")
    }
}

/**
 * Gets the [XMaterial] of this [ItemStack]
 * @return the [XMaterial]
 */
fun ItemStack.xmaterial(): XMaterial = XMaterial.matchXMaterial(this.type)