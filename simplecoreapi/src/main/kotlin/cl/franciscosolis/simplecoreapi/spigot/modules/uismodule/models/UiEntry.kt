package cl.franciscosolis.simplecoreapi.spigot.modules.uismodule.models

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import cl.franciscosolis.simplecoreapi.spigot.modules.uismodule.ui.Ui

/**
 * Representation of an Ui Entry, which is an item in an Ui.
 * @param slot the slot of where the item should be placed
 * @param item the item that should be displayed in the slot (as a function, so it can be dynamic)
 * @param action the action that should be executed when a player clicks on the item. The action should return true if the UI should be closed after the action is executed, false otherwise.
 * @param dynamic if true, this item will be updated at least every tick (20 times per second)
 */
data class UiEntry(
    val slot: Int,
    val item: () -> ItemStack,
    val action: (Ui, Player) -> Unit = { _, _ -> },
    val dynamic: Boolean = false
) {

    /**
     * The cached item, which is the item that will be displayed in the UI.
     * This is used to avoid calling the [item] function every time the UI is updated (unless it's dynamic).
     */
    val cachedItem = item.invoke()
}