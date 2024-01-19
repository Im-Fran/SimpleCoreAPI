package cl.franciscosolis.simplecoreapi.spigot.modules.uismodule.dialog

import com.cryptomorin.xseries.XMaterial
import com.cryptomorin.xseries.messages.ActionBar
import com.cryptomorin.xseries.messages.Titles
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockFertilizeEvent
import org.bukkit.event.block.BlockIgniteEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityInteractEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.hanging.HangingPlaceEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.event.player.PlayerBucketFillEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import cl.franciscosolis.simplecoreapi.global.modules.tasksmodule.models.RecurringTask
import cl.franciscosolis.simplecoreapi.global.modules.translationsmodule.models.Translation
import cl.franciscosolis.simplecoreapi.spigot.SpigotLoader
import cl.franciscosolis.simplecoreapi.spigot.extensions.*
import cl.franciscosolis.simplecoreapi.spigot.modules.tasksmodule.SpigotTasksModule
import cl.franciscosolis.simplecoreapi.spigot.modules.uismodule.models.EditableItemStack
import java.util.*

/**
 * Represents a Dialog (Title, Subtitle and Actionbar message)
 *
 * @param player The player to send the dialog to
 * @param title The title of the dialog. (Optional)
 * @param subtitle The subtitle of the dialog. (Optional)
 * @param actionbar The actionbar message of the dialog. (Optional)
 * @param closeAction The kind of action needed to close the dialog. (Default to ANY_CLICK)
 * @param onChat The action to perform when the player sends a message. If true is returned, the dialog will be closed. (Default to true)
 * @param allowedActions The actions that are allowed while the player is in the dialog. (Default to NONE)
 */
class Dialog(
    val player: Player,
    val title: String? = null,
    val subtitle: String? = null,
    val actionbar: String? = null,
    val closeAction: CloseAction = CloseAction.ANY_CLICK,
    val onChat: ((Player, String) -> Boolean) = { _, _ -> true },
    val allowedActions: List<AllowedActions> = listOf(AllowedActions.NONE)
) : Listener {
    val dialogId: String = "Dialog.${UUID.randomUUID()}"
    private var task: RecurringTask? = null
    private var lastMovementAt = 0L
    private val exitCommand = Translation(
        id = "Dialog.ExitCommand",
        defaultValue = "exit",
        group = "UIsModule",
    )

    private val closeItem = EditableItemStack(
        id = "Dialog.CloseItem",
        itemStack = XMaterial.BARRIER.itemStack()
            .name(
                Translation(
                    id = "Dialog.CloseItem.Name",
                    defaultValue = "Close",
                    group = "UIsModule",
                    mainColor = "&c"
                ).translate()
            )
            .loreLines(
                "&7",
                Translation(
                    id = "Dialog.CloseItem.Lore",
                    defaultValue = "Click **this** to close the dialog.",
                    group = "UIsModule",
                    mainColor = "&7",
                    colors = arrayOf("&c")
                ).translate()
            )
    )

    /**
     * Should the player be able to close the dialog?
     */
    var canBeClosed = true

    /**
     * Was this dialog closed manually or programmatically?
     */
    var closedByPlayer = false
        private set

    private fun send() {
        if (!this.player.isOnline) {
            this.task?.stop()
            return
        }

        if (this.title != null || this.subtitle != null) {
            Titles.sendTitle(
                this.player,
                0, // Fade in
                20, // Stay
                0, // Fade out
                (this.title ?: "&7").bukkitColor(),
                (this.subtitle ?: "&7").bukkitColor()
            )
        } else {
            Titles.clearTitle(this.player)
        }

        val now = System.currentTimeMillis() // Get current time
        val calc =
            now - lastMovementAt // Calculate the difference between the last movement and now. (If the player hasn't moved the calc will be equals to now)
        // Show the close actionbar translation if the player has moved in the last 5 seconds, otherwise show the actionbar
        val actionbar = if (calc < 5000L && calc != now && this.canBeClosed) {
            when (closeAction) {
                CloseAction.CHAT_COMMAND -> closeAction.howToCloseTranslation.translate(
                    placeholders = mapOf("exit_command" to exitCommand.translate(colorize = false))
                )

                CloseAction.HOTBAR_MENU -> closeAction.howToCloseTranslation.translate(
                    placeholders = mapOf("item_material" to "N/A")
                )

                else -> closeAction.howToCloseTranslation.translate()
            }
        } else {
            this.actionbar
        }

        if (actionbar != null) {
            ActionBar.sendActionBar(this.player, actionbar.bukkitColor())
        } else {
            ActionBar.clearActionBar(this.player)
        }
    }

    fun open(): Dialog = this.apply {
        if (this.task == null) {
            this.task = SpigotTasksModule.instance.runTaskTimerAsynchronously(
                delay = 1L,
                period = 5L,
                task = this::send
            )
        }

        this.task?.stop() // Stop the task if it's running
        HandlerList.unregisterAll(this)
        SpigotTasksModule.instance.runTask(this.player::closeInventory)
        SpigotLoader.instance.let { it.server.pluginManager.registerEvents(this, it) }
        this.task?.start()
        this.closedByPlayer = false
    }

    fun close(sendMessage: Boolean = true): Dialog = this.apply {
        this.task?.stop()
        HandlerList.unregisterAll(this)
        this.closedByPlayer = true
        Titles.clearTitle(this.player)
        ActionBar.clearActionBar(this.player)
        if (sendMessage) {
            this.player.sendMessage(
                Translation(
                    id = "Dialog.Closed",
                    defaultValue = "The dialog has been closed.",
                    group = "UIsModule",
                    mainColor = "&c"
                ).translate()
            )
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onInteract(e: PlayerInteractEvent) {
        // If the player is not the same as the one who opened the dialog,
        // or the dialog can't be closed, or the close action is a chat command, then return
        if ((e.player.uniqueId != this.player.uniqueId) || !this.canBeClosed || closeAction == CloseAction.CHAT_COMMAND) {
            if (AllowedActions.NONE in this.allowedActions) {
                e.isCancelled = true
            }
            return
        }

        if (e.action.name.lowercase().startsWith(closeAction.name.lowercase()) || (e.action.name.lowercase()
                .contains("CLICK") && closeAction == CloseAction.ANY_CLICK)
        ) { // This only works with a left or right click

            e.isCancelled = true
            this.close()
            return
        }

        if (closeAction == CloseAction.HOTBAR_MENU) {
            val item = e.player.inventory.itemInMainHand
            if (item.type == XMaterial.AIR.parseMaterial()) {
                return
            }

            e.isCancelled = true
            if (item.isSimilar(this.closeItem.asItemStack())) {
                this.close()
                return
            }
            return
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onInventoryClick(e: InventoryClickEvent) {
        if (e.whoClicked.uniqueId != this.player.uniqueId) {
            return
        }

        if (!this.canBeClosed || closeAction != CloseAction.HOTBAR_MENU) {
            return
        }

        // Cancel the event if the player can't click in their inventory
        e.isCancelled = AllowedActions.INVENTORY_CLICK !in allowedActions

        if (e.currentItem?.isSimilar(this.closeItem.asItemStack()) == true) {
            this.close()
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onInventoryOpen(e: InventoryOpenEvent) {
        if (e.player.uniqueId != this.player.uniqueId) {
            return
        }

        if (!this.canBeClosed || closeAction != CloseAction.HOTBAR_MENU) {
            return
        }

        // Cancel the event if the player can't open their inventory
        e.isCancelled = AllowedActions.INVENTORY_OPEN !in allowedActions
        if (e.isCancelled) {
            // Close the inventory for all viewers
            e.inventory.viewers.forEach { it.closeInventory() }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onMessageReceived(e: AsyncPlayerChatEvent) {
        if (e.player.uniqueId != this.player.uniqueId) {
            return
        }

        e.isCancelled = AllowedActions.CHAT !in allowedActions

        if (!this.canBeClosed || closeAction != CloseAction.CHAT_COMMAND) {
            return
        }

        if (e.message.lowercase().bukkitStripColors()?.startsWith(exitCommand.translate(colorize = false).lowercase()) == true) {
            SpigotTasksModule.instance.runTask {
                if (this.onChat(this.player, e.message)) {
                    this.closedByPlayer = true
                    this.close()
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onMove(e: PlayerInteractEvent) {
        handleInteract(e, e.player, AllowedActions.MOVE)

        this.lastMovementAt = System.currentTimeMillis()
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onDrop(e: PlayerDropItemEvent) = handleInteract(e, e.player, AllowedActions.DROP_ITEM)

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onPickup(e: EntityPickupItemEvent) = handleInteract(e, e.entity, AllowedActions.PICKUP_ITEM)

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onBlockPlace(e: BlockPlaceEvent) = handleInteract(e, e.player, AllowedActions.BLOCK_INTERACT)

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onBlockBreak(e: BlockPlaceEvent) = handleInteract(e, e.player, AllowedActions.BLOCK_INTERACT)

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onBlockFertilize(e: BlockFertilizeEvent) = handleInteract(e, e.player, AllowedActions.BLOCK_INTERACT)

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onBlockIgnite(e: BlockIgniteEvent) = handleInteract(e, e.player, AllowedActions.BLOCK_INTERACT)

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onPlayerBucketEmpty(e: PlayerBucketEmptyEvent) = handleInteract(e, e.player, AllowedActions.BLOCK_INTERACT)

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onPlayerBucketFill(e: PlayerBucketFillEvent) = handleInteract(e, e.player, AllowedActions.BLOCK_INTERACT)

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onHangingPlace(e: HangingPlaceEvent) = handleInteract(e, e.player, AllowedActions.BLOCK_INTERACT)

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onHangingBreakByEntityEvent(e: HangingBreakByEntityEvent) = handleInteract(e, e.remover, AllowedActions.BLOCK_INTERACT)

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onEntityInteract(e: EntityInteractEvent) = handleInteract(e, e.entity, AllowedActions.ENTITY_INTERACT)

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onPlayerInteractEntity(e: PlayerInteractEntityEvent) =  this.handleInteract(e, e.player, AllowedActions.ENTITY_INTERACT)

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onPlayerInteractAtEntity(e: PlayerInteractAtEntityEvent) = this.handleInteract(e, e.player, AllowedActions.ENTITY_INTERACT)

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onEntityDamageByEntity(e: EntityDamageByEntityEvent) = handleInteract(e, e.damager, AllowedActions.ENTITY_INTERACT)

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onCommand(e: PlayerCommandPreprocessEvent) = handleInteract(e, e.player, AllowedActions.COMMAND)

    private fun handleInteract(e: Cancellable, player: Entity?, allowedActions: AllowedActions) {
        if(player == null || player !is Player) {
            return
        }

        if (player.uniqueId != this.player.uniqueId) {
            return
        }

        e.isCancelled = allowedActions !in this.allowedActions
    }
}