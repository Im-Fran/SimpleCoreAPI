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

package cl.franciscosolis.simplecoreapi.bukkit

import cl.franciscosolis.simplecoreapi.SimpleCoreAPI
import cl.franciscosolis.simplecoreapi.bukkit.events.AsyncConfigurationReloadEvent
import cl.franciscosolis.simplecoreapi.bukkit.events.ConfigurationReloadEvent
import cl.franciscosolis.simplecoreapi.bukkit.extensions.callEvent
import cl.franciscosolis.simplecoreapi.bukkit.modules.tasksmodule.BukkitTasksModule
import org.bukkit.plugin.java.JavaPlugin

class BukkitLoader: JavaPlugin() {

    companion object {

        /**
         * Instance of the [BukkitLoader], useful for accessing some bukkit-specific methods.
         */
        lateinit var instance: BukkitLoader
            private set
    }

    override fun onLoad() {
        instance = this
        SimpleCoreAPI(logger = this.logger)
    }

    /**
     * This function should only be called if you need to reload settings.
     * This will fire the [ConfigurationReloadEvent] and [AsyncConfigurationReloadEvent] events.
     */
    fun fireSettingsReload() {
        BukkitTasksModule.instance.runTask {
            callEvent(ConfigurationReloadEvent())
        }

        BukkitTasksModule.instance.runTaskAsynchronously {
            callEvent(AsyncConfigurationReloadEvent())
        }
    }
}