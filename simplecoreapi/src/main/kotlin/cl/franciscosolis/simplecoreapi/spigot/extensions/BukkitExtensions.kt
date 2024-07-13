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

package cl.franciscosolis.simplecoreapi.spigot.extensions

import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.PluginManager

/**
 * Registers an event listener
 * @param listener the listener to register
 * @param plugin the plugin to register the listener for
 * @see PluginManager.registerEvents
 */
fun registerEvent(listener: Listener, plugin: Plugin) =
    Bukkit.getPluginManager().registerEvents(listener, plugin)

/**
 * Calls an event
 * @param event the event to call
 * @see PluginManager.callEvent
 */
fun callEvent(event: Event) =
    Bukkit.getPluginManager().callEvent(event)