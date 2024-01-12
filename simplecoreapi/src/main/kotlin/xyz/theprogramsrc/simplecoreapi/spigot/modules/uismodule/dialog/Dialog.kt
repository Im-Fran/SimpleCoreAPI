package xyz.theprogramsrc.simplecoreapi.spigot.modules.uismodule.dialog

import com.cryptomorin.xseries.XMaterial
import com.cryptomorin.xseries.messages.ActionBar
import com.cryptomorin.xseries.messages.Titles
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerInteractEvent
import xyz.theprogramsrc.simplecoreapi.global.modules.tasksmodule.models.RecurringTask
import xyz.theprogramsrc.simplecoreapi.global.modules.translationsmodule.models.Translation
import xyz.theprogramsrc.simplecoreapi.spigot.SpigotLoader
import xyz.theprogramsrc.simplecoreapi.spigot.extensions.bukkitColor
import xyz.theprogramsrc.simplecoreapi.spigot.extensions.bukkitStripColors
import xyz.theprogramsrc.simplecoreapi.spigot.extensions.itemStack
import xyz.theprogramsrc.simplecoreapi.spigot.extensions.name
import xyz.theprogramsrc.simplecoreapi.spigot.modules.tasksmodule.SpigotTasksModule
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
 */
class Dialog(
    val player: Player,
    val title: String? = null,
    val subtitle: String? = null,
    val actionbar: String? = null,
    val closeAction: CloseAction = CloseAction.ANY_CLICK,
    val onChat: ((Player, String) -> Boolean) = { _, _ -> true },
): Listener{
    val dialogId: String = "Dialog.${UUID.randomUUID()}"
    private var task: RecurringTask? = null
    private var lastMovementAt = 0L
    private val exitCommand = Translation(
        id = "Dialog.ExitCommand",
        defaultValue = "exit",
        group = "UIsModule",
    )

    private val closeItem = XMaterial.BARRIER.itemStack()
        .name("")

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
        if(!this.player.isOnline) {
            this.task?.stop()
            return
        }

        if(this.title != null || this.subtitle != null) {
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
        val calc = now - lastMovementAt // Calculate the difference between the last movement and now. (If the player hasn't moved the calc will be equals to now)
        // Show the close actionbar translation if the player has moved in the last 5 seconds, otherwise show the actionbar
        val actionbar = if(calc < 5000L && calc != now && this.canBeClosed) {
            when(closeAction) {
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

        if(actionbar != null) {
            ActionBar.sendActionBar(this.player, actionbar.bukkitColor())
        } else {
            ActionBar.clearActionBar(this.player)
        }
    }

    fun open(): Dialog = this.apply {
        if(this.task == null){
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
        if(sendMessage) {
            this.player.sendMessage(Translation(
                id = "Dialog.Closed",
                defaultValue = "The dialog has been closed.",
                group = "UIsModule",
                mainColor = "&c"
            ).translate())
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onInteract(e: PlayerInteractEvent) {
        if(e.isAsynchronous)

        // If the player is not the same as the one who opened the dialog, or the dialog can't be closed, or the close action is a chat command, then return
        if((e.player.uniqueId != this.player.uniqueId) || !this.canBeClosed || closeAction == CloseAction.CHAT_COMMAND) {
            return
        }

        if(e.action.name.lowercase().startsWith(closeAction.name.lowercase()) || (e.action.name.lowercase().contains("CLICK") && closeAction == CloseAction.ANY_CLICK)) { // This only works with a left or right click
            this.close()
            return
        }

        if(closeAction == CloseAction.HOTBAR_MENU) {
            // TODO: Check if the item is the same as the one in the config
            return
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onMessageReceived(e: AsyncPlayerChatEvent) {
        if(e.player.uniqueId != this.player.uniqueId) {
            return
        }

        e.isCancelled = true

        if(!this.canBeClosed || closeAction != CloseAction.CHAT_COMMAND) {
            return
        }

        if(e.message.lowercase().bukkitStripColors()?.startsWith(exitCommand.translate(colorize = false).lowercase()) == true) {
            SpigotTasksModule.instance.runTask {
                if(this.onChat(this.player, e.message)) {
                    this.closedByPlayer = true
                    this.close()
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onMove(e: PlayerInteractEvent) {
        if(e.player.uniqueId != this.player.uniqueId) {
            return
        }

        this.lastMovementAt = System.currentTimeMillis()
    }
}