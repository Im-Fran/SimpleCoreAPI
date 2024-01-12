package xyz.theprogramsrc.simplecoreapi.spigot.extensions

import com.cryptomorin.xseries.XMaterial
import org.bukkit.inventory.ItemStack

/**
 * Gets the [ItemStack] of this [XMaterial]
 * @return the item stack
 */
fun XMaterial.itemStack(): ItemStack = this.let {
    if(this == XMaterial.AIR) {
        throw IllegalArgumentException("Cannot create an itemstack of air!")
    }
    val material = this.parseMaterial() ?: throw IllegalArgumentException("${this.name} is an invalid material!")
    ItemStack(material)
}