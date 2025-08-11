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

package cl.franciscosolis.simplecoreapi.paper.modules.uismodule.models

import cl.franciscosolis.simplecoreapi.SimpleCoreAPI
import cl.franciscosolis.simplecoreapi.paper.PaperLoader
import cl.franciscosolis.simplecoreapi.paper.events.AsyncConfigurationReloadEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import cl.franciscosolis.simplecoreapi.modules.filesmodule.config.YmlConfig
import cl.franciscosolis.simplecoreapi.modules.filesmodule.extensions.file
import cl.franciscosolis.simplecoreapi.modules.filesmodule.extensions.folder
import cl.franciscosolis.simplecoreapi.paper.extensions.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import java.io.File

/**
 * Represents an item stack that can be edited and customized in config.
 * @param id The id of the item.
 * @param group The group of the files to store the item. (Default to "UIsModule")
 * @param itemStack The item stack to save.
 */
class EditableItemStack(
    private val id: String,
    private val group: String = "UIsModule",
    private val itemStack: ItemStack
): Listener {
    private val config = YmlConfig(File(SimpleCoreAPI.dataFolder("Items/${if (group.endsWith("/")) group.dropLast(1) else group}"),"Items.yml").file())

    init {
        save()
    }

    private fun save() {
        config.add(id, itemStack.serializeToMap())
        registerEvent(this, PaperLoader.instance)
    }

    /**
     * Gets the editable item as an [ItemStack]
     * @return the item stack
     */
    fun asItemStack(): ItemStack = if (!config.has(id)) {
        itemStack
    } else {
        deserializeItemStack(config.getMapOrAdd(id, itemStack.serializeToMap()))
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onConfigReloadAsync(event: AsyncConfigurationReloadEvent) {
        config.load()
    }

}