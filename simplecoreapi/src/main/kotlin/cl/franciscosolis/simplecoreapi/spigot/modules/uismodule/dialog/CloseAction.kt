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

import cl.franciscosolis.simplecoreapi.global.modules.translationsmodule.models.Translation

/**
 * Represents a CloseAction to close a dialog.
 * @param howToCloseTranslation The translation to show the player how to close the dialog.
 *
 * @see Dialog
 */
enum class CloseAction(
    val howToCloseTranslation: Translation
){
    /**
     * Closes the dialog when the player left-clicks in the air.
     */
    LEFT_CLICK(Translation(
        id = "HowToClose.LeftClick",
        defaultValue = "To close this dialog use **left click**",
        group = "UIsModule",
        mainColor = "&f",
        colors = arrayOf("&b"),
    )),

    /**
     * Closes the dialog when the player right-clicks in the air.
     */
    RIGHT_CLICK(Translation(
        id = "Dialog.HowToClose.RightClick",
        defaultValue = "To close this dialog use **right click**",
        group = "UIsModule",
        mainColor = "&f",
        colors = arrayOf("&b"),
    )),

    /**
     * Closes the dialog when the player executes any kind of click in the air.
     */
    ANY_CLICK(Translation(
        id = "Dialog.Dialog.HowToClose.AnyClick",
        defaultValue = "To close this dialog use **any click**",
        group = "UIsModule",
        mainColor = "&f",
        colors = arrayOf("&b"),
    )),

    /**
     * Closes the dialog when the player types in the chat an exit command (configurable and translatable).
     */
    CHAT_COMMAND(Translation(
        id = "Dialog.HowToClose.ChatCommand",
        defaultValue = "To close this dialog type **{exit_command}** in the chat",
        group = "UIsModule",
        mainColor = "&f",
        colors = arrayOf("&b"),
    )),

    /**
     * An item is displayed in the player's hotbar with the ability
     * to close the dialog by clicking on it in the inventory or air.
     */
    HOTBAR_MENU(Translation(
        id = "Dialog.HowToClose.HotbarMenu",
        defaultValue = "To close this dialog use the **{item_material}** in your hotbar",
        group = "UIsModule",
        mainColor = "&f",
        colors = arrayOf("&b"),
    )),
}