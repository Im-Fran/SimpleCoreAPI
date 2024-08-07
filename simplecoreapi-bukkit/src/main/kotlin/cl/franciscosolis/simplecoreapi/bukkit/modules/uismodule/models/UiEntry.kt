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

package cl.franciscosolis.simplecoreapi.bukkit.modules.uismodule.models

import cl.franciscosolis.simplecoreapi.bukkit.extensions.itemStack
import cl.franciscosolis.simplecoreapi.bukkit.extensions.name
import cl.franciscosolis.simplecoreapi.bukkit.extensions.setGlowing
import cl.franciscosolis.simplecoreapi.bukkit.modules.uismodule.ui.Ui
import com.cryptomorin.xseries.XMaterial
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * Representation of an Ui Entry, which is an item in an Ui.
 * @param item the item that should be displayed in the slot (as a function, so it can be dynamic)
 * @param action the action that should be executed when a player clicks on the item. The action should return true if the UI should be closed after the action is executed, false otherwise.
 * @param dynamic if true, this item will be updated at least every tick (20 times per second)
 */
data class UiEntry(
    val item: () -> ItemStack,
    val action: (Ui, Player) -> Unit = { _, _ -> },
    val dynamic: Boolean = false,
) {

    companion object {

        /**
         * Creates an empty UiEntry
         * @param item the item that should be displayed. Defaults to a glowing gray stained-glass pane without name.
         */
        fun empty(item: () -> ItemStack = {
            XMaterial.GRAY_STAINED_GLASS_PANE.itemStack()
                .name("&7")
                .setGlowing(glowing = true)
        }) = UiEntry(item = item)
    }
}