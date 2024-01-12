package xyz.theprogramsrc.simplecoreapi.spigot.modules.uismodule.models

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import xyz.theprogramsrc.simplecoreapi.global.modules.filesmodule.config.YmlConfig
import xyz.theprogramsrc.simplecoreapi.spigot.SpigotLoader
import xyz.theprogramsrc.simplecoreapi.spigot.events.AsyncConfigurationReloadEvent
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
    private val config = YmlConfig(File("SimpleCoreAPI/items/${if (group.endsWith("/")) group.dropLast(1) else group}/Items.yml"))

    init {
        save()
    }

    private fun save() {
        config.getMapOrAdd(id, itemStack.serialize())
        SpigotLoader.instance.server.pluginManager.registerEvents(this, SpigotLoader.instance)
    }

    /**
     * Gets the editable item as an [ItemStack]
     * @return the item stack
     */
    fun asItemStack(): ItemStack = if (!config.has(id)) {
        itemStack
    } else {
        ItemStack.deserialize(config.getMap(id))
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onConfigReloadAsync(event: AsyncConfigurationReloadEvent) {
        config.load()
    }

}