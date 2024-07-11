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

import cl.franciscosolis.simplecoreapi.global.module.requireModule
import cl.franciscosolis.simplecoreapi.global.modules.tasksmodule.models.RecurringTask
import cl.franciscosolis.simplecoreapi.spigot.SpigotLoader
import cl.franciscosolis.simplecoreapi.spigot.extensions.bukkitColor
import cl.franciscosolis.simplecoreapi.spigot.extensions.bukkitStripColors
import cl.franciscosolis.simplecoreapi.spigot.extensions.registerEvent
import cl.franciscosolis.simplecoreapi.spigot.modules.tasksmodule.SpigotTasksModule
import cl.franciscosolis.simplecoreapi.spigot.modules.uismodule.models.UiModel
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.Inventory

/**
 * Representation of a UI that will be shown to a player
 *
 * @param player the player that should see the UI
 * @param shouldCloseInventory whether the UI should be able to be closed by the player or not (usually when pressing the ESC or inventory key)
 * @param onBuild the function that will be called when the UI is built
 */
open class Ui(
    val player: Player,
    val shouldCloseInventory: Boolean = true,
    val onBuild: UiModel.() -> Unit = {}
): Listener {

    private var stopUpdateTask = false

    private var actions = mutableMapOf<Int, (Ui, Player) -> Unit>()
    private var updateTask: RecurringTask? = null
    var inventory: Inventory? = null
        private set

    init {
        check(player.isOnline) { "Player must be online" } // Check that the player is online
        updateTask = requireModule<SpigotTasksModule>().runTaskTimer(period = 2L, delay = 0L){ // Initialize the update task (every .1 second and no delay)
            if (stopUpdateTask || player.isOnline.not()) { // If the UI is stopped or the player is not online
                this.updateTask?.stop() // Stop the task
                HandlerList.unregisterAll(this@Ui) // Unregister the listener
                return@runTaskTimer
            }

            val model = UiModel()
            this.onBuild(model)
            if(this.inventory == null || this.player.openInventory.title != model.title.bukkitColor() || this.inventory?.size != (model.rows.times(9))) {
                this.inventory = model.build(inventory = this.inventory)
                this.player.openInventory(this.inventory!!)
            }

            model.build(inventory = this.inventory, dynamicOnly = true)

            model.entries.forEach { (slot, entry) -> actions[slot] = entry.action }
            this.player.updateInventory()
        }

        registerEvent(this, SpigotLoader.instance)
    }

    /**
     * Closes the UI
     * @return the Ui instance
     */
    fun close() = this.apply {
        this.updateTask?.stop()
        this.inventory = null
        HandlerList.unregisterAll(this)
    }

    /**
     * Opens the UI (if it's not already open)
     * @return the Ui instance
     */
    fun open() = this.apply {
        if(this.inventory != null) {
            return@apply
        }

        this.stopUpdateTask = false
        this.updateTask?.start()
        registerEvent(this, SpigotLoader.instance)
    }

    /**
     * Prevents the Ui from reopening
     * @return the Ui instance
     */
    fun stop() = this.apply {
        this.stopUpdateTask = true
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onClick(e: InventoryClickEvent) {
        if(e.whoClicked.uniqueId != this.player.uniqueId || e.inventory != this.inventory) {
            return
        }

        e.isCancelled = true
        val action = actions[e.slot] ?: return
        action.invoke(this, this.player)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onDrag(e: InventoryDragEvent) {
        if(e.whoClicked.uniqueId != this.player.uniqueId || e.inventory != this.inventory) {
            return
        }

        e.isCancelled = true
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        if(e.player.uniqueId != this.player.uniqueId) {
            return
        }

        this.close()
    }
}