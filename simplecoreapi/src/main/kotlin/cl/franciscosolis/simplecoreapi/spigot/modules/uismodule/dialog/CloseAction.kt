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