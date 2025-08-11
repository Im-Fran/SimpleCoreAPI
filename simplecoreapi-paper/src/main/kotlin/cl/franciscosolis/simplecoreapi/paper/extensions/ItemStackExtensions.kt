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

package cl.franciscosolis.simplecoreapi.paper.extensions

import cl.franciscosolis.simplecoreapi.paper.modules.uismodule.models.SimpleEnchantment
import com.cryptomorin.xseries.XMaterial
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
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
val ItemStack.name: Component?
    get() = this.itemMeta?.displayName()

/**
 * Sets the name of this [ItemStack]
 * @param name the name of the item
 * @return this [ItemStack]
 */
fun ItemStack.withName(name: Component): ItemStack = this.apply {
    this.editMeta {
        it.displayName(name)
    }
}

/**
 * Sets the lore of this [ItemStack]
 * @param lore the lore of the item
 * @param append if the lore should be appended or replaced
 * @return this [ItemStack]
 */
fun ItemStack.withLore(lore: List<Component>, append: Boolean = false): ItemStack = this.apply {
    if(append) {
        this.lore((this.lore() ?: emptyList<Component>())
            .toMutableList()
            .asSequence()
            .plus(lore)
            .toList())
    } else {
        this.lore(lore)
    }
}

/**
 * Sets the lore of this [ItemStack]
 * @param lore the lore of the item
 * @param append if the lore should be appended or replaced
 * @return this [ItemStack]
 */
fun ItemStack.withLore(vararg lore: Component, append: Boolean = false): ItemStack = this.withLore(lore.toList(), append)

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
 * Gets the enchantments of this [ItemStack] as [SimpleEnchantment]
 * @return the enchantments of the item
 */
val ItemStack.simpleEnchantments: List<SimpleEnchantment>
    get() = this.enchantments.map { SimpleEnchantment(it.key, it.value) }

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
 * Gets the glowing effect of this [ItemStack]
 * @return if the item is glowing
 */
fun ItemStack.isGlowing(): Boolean = this.itemMeta?.let {
    it.hasEnchantmentGlintOverride() && it.enchantmentGlintOverride
} ?: false

/**
 * Sets the damage to this [ItemStack]
 * @param damage the damage to the item
 * @return this [ItemStack]
 */
fun ItemStack.damage(damage: Int): ItemStack = this.apply {
    this.itemMeta = this.itemMeta?.apply {
        check(this is Damageable) { "Item is not damageable!" }
        this.damage = damage
    }
}

/**
 * Gets the damage of this [ItemStack].
 * First check if the item is damageable, then check if it has a damage value.
 * @see [ItemStack.hasDamage]
 * @return the damage of the item
 */
val ItemStack.damage: Int
    get() = this.itemMeta?.let {
        check(it is Damageable) { "Item is not damageable!" }
        check(it.hasDamageValue()) { "Item has no damage value!" }
        return it.damage
    } ?: 0

/**
 * Checks if the item has a damage value
 * @return if the item has a damage value
 */
fun ItemStack.hasDamage(): Boolean = this.itemMeta?.let {
    return it is Damageable && it.hasDamageValue()
} ?: false

/**
 * Sets the color if the item is leather armor
 * @param color the color of the item
 */
fun ItemStack.color(color: Color): ItemStack = this.apply {
    this.itemMeta = this.itemMeta?.apply {
        check(type == Material.LEATHER_BOOTS || type == Material.LEATHER_CHESTPLATE || type == Material.LEATHER_HELMET || type == Material.LEATHER_LEGGINGS) { "Item is not leather armor!" }
        check(this is LeatherArmorMeta) { "Item is not leather armor!" }
        setColor(color)
    }
}

/**
 * Checks if the item can be set a color
 * @return if the item can be colored
 */
val ItemStack.canColor: Boolean
    get() = this.itemMeta?.let {
        type == Material.LEATHER_BOOTS || type == Material.LEATHER_CHESTPLATE || type == Material.LEATHER_HELMET || type == Material.LEATHER_LEGGINGS
    } ?: false

/**
 * Gets the color of this [ItemStack]
 * @return the color of the item
 */
val ItemStack.color: Color?
    get() = this.itemMeta?.let {
        check(it is LeatherArmorMeta) { "Item is not leather armor!" }
        return it.color
    }

/**
 * Gets the [XMaterial] of this [ItemStack]
 * @return the [XMaterial]
 */
fun ItemStack.xmaterial(): XMaterial = XMaterial.matchXMaterial(this.type)

/**
 * Serializes the [ItemStack] into a [Map].
 * @return the serialized item
 */
fun ItemStack.serializeToMap(): Map<String, Any> {
    val map = mutableMapOf<String, Any>()
    map["Material"] = xmaterial().name
    name?.let { map["Name"] = LegacyComponentSerializer.legacyAmpersand().serialize(it) }
    lore()?.map { LegacyComponentSerializer.legacyAmpersand().serialize(it) }?.let { map["Lore"] = it }
    if(amount > 1) map["Amount"] = amount
    if(flags.isNotEmpty()) map["Flags"] = flags.map { it.name }
    if(simpleEnchantments.isNotEmpty()) map["Enchants"] = simpleEnchantments.map { it.toString() }
    if(isGlowing()) map["Glowing"] = isGlowing()
    if(hasDamage()) map["Damage"] = (itemMeta as? Damageable)?.damage ?: 0
    if (canColor && color != null) map["Color"] = "hex:${color!!.asHex()}"

    return map
}

/**
 * Deserializes the [ItemStack] from a [Map].
 */
fun deserializeItemStack(map: Map<String, Any>): ItemStack {
    checkNotNull(map["Material"]) { "Item type is required!" }
    return XMaterial.valueOf(map["Material"] as String)
        .itemStack()
        .amount(map["Amount"] as? Int ?: 1)
        .addFlags(*(map["Flags"] as? List<*>)?.map { ItemFlag.valueOf(it as String) }?.toTypedArray() ?: emptyArray())
        .addEnchantments(*(map["Enchants"] as? List<*>)?.map { SimpleEnchantment.fromString(it as String) }?.toTypedArray() ?: emptyArray())
        .apply {
            if(map.containsKey("Name")) withName(LegacyComponentSerializer.legacyAmpersand().deserialize(map["Name"] as String))
            if(map.containsKey("Lore")) withLore((map["Lore"] as List<*>).map { LegacyComponentSerializer.legacyAmpersand().deserialize(it as String) })

            if(map.containsKey("Glowing")) setGlowing(map["Glowing"] as Boolean)
            if (map.containsKey("Damage")) damage(map["Damage"] as Int)

            if (map.containsKey("Color")) {
                val color = (map["Color"] as String).split(":")
                if(color[0] == "hex") {
                    color(color[1].hexToColor())
                } else if (color[0] == "rgb") {
                    val rgb = color[1].split(",")
                    color(Color.fromRGB(rgb[0].toInt(), rgb[1].toInt(), rgb[2].toInt()))
                }
            }
        }
}