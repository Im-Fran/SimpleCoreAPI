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

package cl.franciscosolis.simplecoreapi.paper

import cl.franciscosolis.simplecoreapi.SimpleCoreAPI
import cl.franciscosolis.simplecoreapi.modules.translationsmodule.models.Translation
import cl.franciscosolis.simplecoreapi.paper.events.AsyncConfigurationReloadEvent
import cl.franciscosolis.simplecoreapi.paper.events.ConfigurationReloadEvent
import cl.franciscosolis.simplecoreapi.paper.extensions.asComponent
import cl.franciscosolis.simplecoreapi.paper.extensions.callEvent
import cl.franciscosolis.simplecoreapi.paper.extensions.registerEvent
import cl.franciscosolis.simplecoreapi.paper.modules.uismodule.dialog.AllowedActions
import cl.franciscosolis.simplecoreapi.paper.modules.uismodule.dialog.CloseAction
import cl.franciscosolis.simplecoreapi.paper.modules.uismodule.dialog.Dialog
import cl.franciscosolis.simplecoreapi.utils.text.Text
import cl.franciscosolis.simplecoreapi.utils.text.TextColor
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

class PaperLoader: JavaPlugin() {

    companion object {

        /**
         * Instance of the [PaperLoader], useful for accessing some paper-specific methods.
         */
        lateinit var instance: PaperLoader
            private set
    }

    override fun onLoad() {
        instance = this
        SimpleCoreAPI(logger = this.logger)
    }

    override fun onEnable() {
        // Create a class that implements listener, and has a function 'onJoin' to handle join events.
        registerEvent(listener = object : Listener {
            @EventHandler
            fun onJoin(e: PlayerJoinEvent) {
                e.joinMessage(null)

                val title = Text("Welcome to **MyServer**!", mainColor = TextColor.AQUA, colors = arrayOf(TextColor.GOLD))
                val subtitle = Text("We hope you enjoy your stay!", mainColor = TextColor.GREEN)
                val actionbar = Text("Write in the chat **Accept** in order to accept our rules!", mainColor = TextColor.RED, colors = arrayOf(TextColor.LIGHT_PURPLE))

                Dialog(
                    player = e.player,
                    title = title.asComponent(),
                    subtitle = subtitle.asComponent(),
                    actionbar = actionbar.asComponent(),
                    closeAction = CloseAction.HOTBAR_MENU,
                    onChat = { _, input ->
                        input.lowercase() == "test"
                    },
                ).apply {
                    SimpleCoreAPI.logger.info("Dialog $dialogId opened!")
                    open()
                }
            }
        }, plugin = this)
    }

    override fun onDisable() {
        // Disable modules
        SimpleCoreAPI.disableTasks.forEach { it() }
    }

    /**
     * This function should only be called if you need to reload settings.
     * This will fire the [ConfigurationReloadEvent] and [AsyncConfigurationReloadEvent] events.
     */
    fun fireSettingsReload() {
        callEvent(ConfigurationReloadEvent())
        callEvent(AsyncConfigurationReloadEvent())
    }
}