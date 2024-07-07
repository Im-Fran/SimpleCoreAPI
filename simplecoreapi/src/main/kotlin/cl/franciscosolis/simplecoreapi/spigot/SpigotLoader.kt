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

package cl.franciscosolis.simplecoreapi.spigot

import org.bukkit.plugin.java.JavaPlugin
import cl.franciscosolis.simplecoreapi.global.SimpleCoreAPI
import cl.franciscosolis.simplecoreapi.global.module.requireModule
import cl.franciscosolis.simplecoreapi.spigot.events.AsyncConfigurationReloadEvent
import cl.franciscosolis.simplecoreapi.spigot.events.ConfigurationReloadEvent
import cl.franciscosolis.simplecoreapi.spigot.extensions.*
import cl.franciscosolis.simplecoreapi.spigot.modules.tasksmodule.SpigotTasksModule
import cl.franciscosolis.simplecoreapi.spigot.modules.uismodule.UIsModule
import cl.franciscosolis.simplecoreapi.spigot.modules.uismodule.models.UiEntry
import cl.franciscosolis.simplecoreapi.spigot.modules.uismodule.ui.Ui
import com.cryptomorin.xseries.XMaterial
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent

/**
 * Representation of the Spigot plugin loader.
 */
open class SpigotLoader: JavaPlugin(), Listener {

    companion object {

        /**
         * Instance of the SpigotLoader, useful for accessing some spigot-specific methods.
         */
        lateinit var instance: SpigotLoader
            private set
    }

    override fun onLoad() {
        instance = this

        SimpleCoreAPI()
    }

    override fun onEnable() {
        super.onEnable()

        requireModule<UIsModule>()
        registerEvent(this,this)
    }

    private val alwaysALevel = {
        XMaterial.EXPERIENCE_BOTTLE.itemStack()
            .name("&a&lAlways A Level")
            .loreLines(
                "&7",
                "&7This item will give you",
                "&7a level every time you",
                "&7right-click it."
            )
    }

    private val emptyPane = {
        XMaterial.GRAY_STAINED_GLASS_PANE.itemStack()
            .name("&7")
            .loreLines("&7")
            .setGlowing(glowing = true)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val entries = mutableListOf<UiEntry>()
        // Add empty panes
        repeat(27) { entries.add(UiEntry(slot =  it, item = emptyPane)) }
        entries[13] = UiEntry(slot = 13, item = alwaysALevel, action = { ui, player ->
            player.inventory.addItem(alwaysALevel())
            ui.close()
        })

        Ui(
            title = "&6&lMy Custom UI!",
            rows = 3,
            player = event.player,
            entries = entries
        ).open()
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if(event.player.inventory.itemInMainHand.isSimilar(alwaysALevel()) || event.player.inventory.itemInOffHand.isSimilar(alwaysALevel())) {
            event.isCancelled = true
            event.player.level += 1
        }
    }

    /**
     * This function should only be called if you need to reload settings.
     * This will fire the [ConfigurationReloadEvent] and [AsyncConfigurationReloadEvent] events.
     */
    fun fireSettingsReload() {
        SpigotTasksModule.instance.runTask {
            server.pluginManager.callEvent(ConfigurationReloadEvent())
        }

        SpigotTasksModule.instance.runTaskAsynchronously {
            server.pluginManager.callEvent(AsyncConfigurationReloadEvent())
        }
    }

}