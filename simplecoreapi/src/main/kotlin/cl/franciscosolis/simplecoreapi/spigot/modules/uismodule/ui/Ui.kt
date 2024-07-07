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

package cl.franciscosolis.simplecoreapi.spigot.modules.uismodule.ui

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import cl.franciscosolis.simplecoreapi.spigot.SpigotLoader
import cl.franciscosolis.simplecoreapi.spigot.extensions.bukkitColor
import cl.franciscosolis.simplecoreapi.spigot.extensions.registerEvent
import cl.franciscosolis.simplecoreapi.spigot.modules.uismodule.models.UiEntry

/**
 * Representation of a "raw" UI, meaning there are no
 * animations or anything, just a simple UI.
 *
 * @param player the player that should see the UI
 * @param title the title of the UI
 * @param rows the amount of rows the UI should have
 * @param entries a list of [UiEntry] that will contain the items of the UI
 * @param shouldCloseInventory whether the UI should be able to be closed by the player or not (usually when pressing the ESC or inventory key)
 */
class Ui(
    val player: Player,
    val title: String,
    val rows: Int = 1,
    val entries: List<UiEntry>,
    val shouldCloseInventory: Boolean = true
): Listener {

    private val mappedEntries = entries.associateBy { it.slot }
    val inventory: Inventory

    init {
        check(rows in 1..6) { "Invalid amount of rows: $rows. Must be between 1 and 6" }
        check(title.bukkitColor().isNotBlank()) { "Invalid title: $title" }
        check(entries.isNotEmpty()) { "Entries cannot be empty" }
        inventory = Bukkit.createInventory(null, rows * 9, title.bukkitColor())
    }

    /**
     * Opens the UI to the player
     */
    fun open() {
        if(!inventory.isEmpty) {
            HandlerList.unregisterAll(this)
        }
        inventory.clear()
        mappedEntries.forEach { (slot, entry) ->
            check(slot in 0 until inventory.size) { "Invalid slot: $slot. Must be between 0 and ${inventory.size}" }
            inventory.setItem(slot, entry.cachedItem)
        }
        registerEvent(this, SpigotLoader.instance)
        player.openInventory(inventory)
    }

    /**
     * Closes the UI to the player
     */
    fun close() {
        HandlerList.unregisterAll(this)
        player.closeInventory()
    }

    @EventHandler
    fun onClick(e: InventoryClickEvent) {
        if(e.whoClicked.uniqueId != player.uniqueId && e.clickedInventory != inventory) {
            return
        }

        val slot = e.slot

        if(mappedEntries[slot]?.cachedItem?.isSimilar(e.currentItem) != true) {
            return
        }

        e.isCancelled = true
        mappedEntries[slot]?.action?.invoke(this, player)
    }

    @EventHandler
    fun onClose(e: InventoryClickEvent) {
        if (e.whoClicked.uniqueId != player.uniqueId && e.clickedInventory != inventory) {
            return
        }

        if (!shouldCloseInventory) {
            open()
        }
    }
}