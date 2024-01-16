package xyz.theprogramsrc.simplecoreapi.spigot.extensions

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