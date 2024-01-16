package xyz.theprogramsrc.simplecoreapi.spigot.modules.uismodule.ui

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import xyz.theprogramsrc.simplecoreapi.spigot.SpigotLoader
import xyz.theprogramsrc.simplecoreapi.spigot.extensions.bukkitColor
import xyz.theprogramsrc.simplecoreapi.spigot.extensions.registerEvent
import xyz.theprogramsrc.simplecoreapi.spigot.modules.uismodule.models.UiEntry

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