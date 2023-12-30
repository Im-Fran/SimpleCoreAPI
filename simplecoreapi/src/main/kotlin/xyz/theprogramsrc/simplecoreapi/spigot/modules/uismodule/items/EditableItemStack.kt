package xyz.theprogramsrc.simplecoreapi.spigot.modules.uismodule.items

import com.cryptomorin.xseries.XMaterial
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.AxolotlBucketMeta
import org.bukkit.inventory.meta.BannerMeta
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta
import xyz.theprogramsrc.simplecoreapi.global.modules.filesmodule.config.YmlConfig
import xyz.theprogramsrc.simplecoreapi.global.modules.filesmodule.extensions.folder
import xyz.theprogramsrc.simplecoreapi.spigot.extensions.asHex
import xyz.theprogramsrc.simplecoreapi.spigot.extensions.bukkitToColor
import java.io.File
import java.util.*

/**
 * Represents an item saved into a configuration file and can be edited.
 * @param id The id of the item.
 * @param group The group of the files to store the item. (Default to "UIsModule")
 * @param itemStack The item stack to save.
 */
class EditableItemStack(
    val id: String,
    val group: String = "UIsModule",
    val itemStack: ItemStack
){

    /**
     * The configuration file of the item.
     */
    private val config = YmlConfig(File(File("SimpleCoreAPI/Items/").folder(), "${if (group.endsWith("/")) group.dropLast(1) else group}.yml".replace(" ", "_")))

    init {
        save()
    }

    private fun save() {
        if(config.has(id)){
           return
        }

        config.set("$id.Material", itemStack.xmaterial().name)
        config.set("$id.Amount", itemStack.amount)
        config.set("$id.Enchantments", itemStack.enchantments.map { (enchantment, level) ->
            "${enchantment.key}:$level" // format: key:namespace:level
        })
        config.set("$id.Flags", itemStack.flags.map { it.name })

        val meta = itemStack.itemMeta
        if(!itemStack.hasItemMeta() || meta == null){
            return
        }

        itemStack.name?.let {
            config.set("$id.Name", it)
        }

        meta.lore?.let {
            config.set("$id.Lore", it)
        }

        config.set("$id.Flags", meta.itemFlags.map { it.name })
        if(meta is Damageable) {
            config.set("$id.Durability", meta.damage)
        }

        when (meta) {
            is LeatherArmorMeta -> {
                config.set("$id.Color", meta.color.asHex())
            }

            is SkullMeta -> {
                config.set("$id.SkullOwner", meta.owningPlayer?.uniqueId ?: "")
                config.set("$id.PlayerProfile.Id", meta.ownerProfile?.uniqueId?.toString() ?: "")
                config.set("$id.PlayerProfile.Name", meta.ownerProfile?.name ?: "")
                config.set("$id.NoteBlockSound", meta.noteBlockSound.toString())
            }

            is AxolotlBucketMeta -> {
                config.set("$id.AxolotlBucketVariant", meta.variant.toString())
            }

            is BannerMeta -> {
                config.set("$id.BannerPatterns", meta.patterns.map { pattern ->
                    "${pattern.color.name}:${pattern.pattern.name}"
                })
            }

            is BookMeta -> {
                meta.title?.let {
                    config.set("$id.BookTitle", it)
                }
                meta.author?.let {
                    config.set("$id.BookAuthor", it)
                }
                config.set("$id.BookPages", meta.pages)
            }
        }

    }

    /**
     * Gets the editable item as an [ItemStack]
     * @return the item stack
     */
    fun asItemStack(): ItemStack {
        if(!config.has(id)) {
            return itemStack
        }

        return XMaterial.valueOf(config.getString("$id.Material"))
            .itemStack()
            .amount(config.getInt("$id.Amount"))
            .addEnchantments(*config.getStringList("$id.Enchantments").mapNotNull { enchantmentData ->
                val split = enchantmentData.split(":")
                val enchantmentKey = "${split[0]}:${split[1]}"
                val level = split[2].toInt()

                val enchantment = Enchantment.getByKey(NamespacedKey.fromString(enchantmentKey))
                if(enchantment != null) {
                    SimpleEnchantment(enchantment, level)
                } else {
                    null
                }
            }.toTypedArray())
            .addFlags(*config.getStringList("$id.Flags").mapNotNull {
                try {
                    ItemFlag.valueOf(it)
                } catch (_: Exception) {
                    null
                }
            }.toTypedArray())
            .name(config.getString("$id.Name"))
            .loreLines(*config.getStringList("$id.Lore").toTypedArray())
            .damage(config.getInt("$id.Durability"))
            .apply {
                if(config.has("$id.Color")) {
                    color(config.getString("$id.Color").bukkitToColor())
                }

                if(config.has("$id.SkullOwner")) {
                    val skullMeta = this.itemMeta as SkullMeta
                    skullMeta.owningPlayer = Bukkit.getOfflinePlayer(UUID.fromString(config.getString("$id.SkullOwner")))
                    skullMeta.ownerProfile = Bukkit.getServer().createPlayerProfile(UUID.fromString(config.getString("$id.PlayerProfile.Id")), config.getString("$id.PlayerProfile.Name"))
                    skullMeta.noteBlockSound = NamespacedKey.fromString(config.getString("$id.NoteBlockSound"))
                    this.itemMeta = skullMeta
                }
            }
    }
}
