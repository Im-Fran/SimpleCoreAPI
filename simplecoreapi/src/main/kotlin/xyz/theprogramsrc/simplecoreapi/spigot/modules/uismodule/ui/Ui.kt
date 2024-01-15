package xyz.theprogramsrc.simplecoreapi.spigot.modules.uismodule.ui

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import xyz.theprogramsrc.simplecoreapi.spigot.modules.uismodule.ui.events.UiClickEvent

/**
 * Representation of a "raw" UI, meaning there are no
 * animations or anything, just a simple UI.
 *
 * @param player the player that should see the UI
 * @param title the title of the UI
 * @param rows the amount of rows the UI should have
 * @param items the items that should be displayed in the UI
 * @param actions the actions that should be executed when a player clicks on an item
 */
class Ui(
    val player: Player,
    val title: String,
    val rows: Int = 1,
    val items: Map<Int, ItemStack> = emptyMap(),
    val actions: Map<Int, (UiClickEvent) -> Boolean> = emptyMap(),
) {
}