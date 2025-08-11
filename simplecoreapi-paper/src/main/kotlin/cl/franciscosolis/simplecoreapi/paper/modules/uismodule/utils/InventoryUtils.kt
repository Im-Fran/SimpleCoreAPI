package cl.franciscosolis.simplecoreapi.paper.modules.uismodule.utils

import cl.franciscosolis.simplecoreapi.SimpleCoreAPI
import cl.franciscosolis.simplecoreapi.modules.filesmodule.config.JsonConfig
import cl.franciscosolis.simplecoreapi.paper.modules.uismodule.models.EditableItemStack
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.bukkit.entity.Player
import java.io.File

fun savePlayerInventory(player: Player) {
    val json = JsonConfig(File(SimpleCoreAPI.dataFolder("/PlayerData/${player.uniqueId}"), "Inventory.json"))
    val items = JsonArray()
    for(index in 0..35) {
        val item = player.inventory.getItem(index) ?: continue
        items.add(JsonObject().apply {
            addProperty("index", index)
            val itemMap = item.serialize().mapValues { EditableItemStack }
        })
    }
}