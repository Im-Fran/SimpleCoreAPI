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
import cl.franciscosolis.simplecoreapi.spigot.events.AsyncConfigurationReloadEvent
import cl.franciscosolis.simplecoreapi.spigot.events.ConfigurationReloadEvent
import cl.franciscosolis.simplecoreapi.spigot.modules.tasksmodule.SpigotTasksModule

/**
 * Representation of the Spigot plugin loader.
 */
open class SpigotLoader: JavaPlugin() {

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