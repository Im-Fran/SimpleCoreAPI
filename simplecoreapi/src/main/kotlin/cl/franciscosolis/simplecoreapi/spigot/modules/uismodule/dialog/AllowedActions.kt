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

package cl.franciscosolis.simplecoreapi.spigot.modules.uismodule.dialog

/**
 * Represents the actions that are allowed while the player is
 * in a dialog.
 *
 * @see Dialog
 */
enum class AllowedActions {

    /**
     * The player can't do anything while in the dialog.
     */
    NONE,

    /**
     * The player can move while in the dialog.
     */
    MOVE,

    /**
     * The player can open their inventory while in the dialog.
     */
    INVENTORY_OPEN,

    /**
     * The player can click in their inventory while in the dialog.
     */
    INVENTORY_CLICK,

    /**
     * The player can drop items while in the dialog.
     */
    DROP_ITEM,

    /**
     * The player can pick up items while in the dialog.
     */
    PICKUP_ITEM,

    /**
     * The player can interact with entities while in the dialog.
     */
    ENTITY_INTERACT,

    /**
     * The player can interact with blocks while in the dialog.
     */
    BLOCK_INTERACT,

    /**
     * The player can chat while in the dialog.
     *
     * WARNING: Be careful with this one, as it can leak information into
     * the chat.
     */
    CHAT,

    /**
     * The player can execute commands while in the dialog.
     */
    COMMAND,
}