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