package xyz.theprogramsrc.simplecoreapi.spigot.modules.uismodule.ui.events

import org.bukkit.entity.Player
import xyz.theprogramsrc.simplecoreapi.spigot.modules.uismodule.ui.Ui

/**
 * Represents an event fired when a player clicks on a UI
 */
interface UiClickEvent {

    /**
     * Gets the slot that was clicked
     * @return the slot
     */
    fun getSlot(): Int

    /**
     * Gets the player that clicked
     * @return the player
     */
    fun getPlayer(): Player

    /**
     * Gets the UI that was clicked
     * @return the UI
     */
    fun getUi(): Ui
}