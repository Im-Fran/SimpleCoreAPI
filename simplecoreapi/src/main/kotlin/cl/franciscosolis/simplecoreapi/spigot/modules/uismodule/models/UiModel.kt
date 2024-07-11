package cl.franciscosolis.simplecoreapi.spigot.modules.uismodule.models

import cl.franciscosolis.simplecoreapi.spigot.extensions.bukkitColor
import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory

class UiModel {

    /**
     * The title of the Model
     */
    var title: String = "&6&lUnnamed UI"

    /**
     * The rows count of the model
     */
    var rows: Int = 3
        set(value) = if (value in 1..6) field = value else throw IllegalArgumentException("Rows must be between 1 and 6")

    /**
     * The entries of the model
     */
    val entries = mutableMapOf<Int, UiEntry>()

    /**
     * Clears the entries of the model
     * @return The UiModel
     */
    fun clearEntries(): UiModel = this.apply {
        entries.clear()
    }

    /**
     * Sets an entry to the model
     * @param slot The slot of the entry
     * @param entry The entry to add
     * @return The UiModel
     */
    fun set(slot: Int, entry: UiEntry): UiModel = this.apply {
        entries[slot] = entry
    }

    /**
     * Adds an entry to the model, only if there is an empty slot
     * @param entry The entry to add
     * @return The UiModel
     */
    fun add(entry: UiEntry): UiModel = this.apply {
        // Find first empty slot
        val slot = (0 until (rows.times(9))).firstOrNull { !entries.containsKey(it) } ?: return@apply
        entries[slot] = entry
    }

    /**
     * Removes an entry from the model
     * @param slot The slot of the entry
     * @return The UiModel
     */
    fun remove(slot: Int): UiModel = this.apply {
        entries.remove(slot)
    }

    /**
     * Fills the model with the given entry
     * @param entry The entry to fill the model with
     * @return The UiModel
     */
    fun fill(entry: UiEntry): UiModel = this.apply {
        (0 until (rows.times(9))).forEach { slot -> set(slot, entry) }
    }

    /**
     * Fills the empty slots of the model with the given entry
     * @param entry The entry to fill the model with
     * @return The UiModel
     */
    fun fillEmpty(entry: UiEntry): UiModel = this.apply {
        (0 until (rows.times(9))).filter { !entries.containsKey(it) }.forEach { slot -> set(slot, entry) }
    }

    /**
     * Builds the model into a Bukkit Inventory, or applies the entries to the given inventory
     * @param inventory The inventory to apply the entries to (optional)
     * @param dynamicOnly If true, only dynamic entries will be built, otherwise all entries will be built
     * @return The Inventory
     */
    fun build(inventory: Inventory?, dynamicOnly: Boolean = false): Inventory = (inventory ?: Bukkit.createInventory(null, rows.times(9), title.bukkitColor())).apply {
        entries.filter { it.value.dynamic || !dynamicOnly }.forEach { (slot, entry) ->
            setItem(slot, entry.item.invoke())
        }
    }

}