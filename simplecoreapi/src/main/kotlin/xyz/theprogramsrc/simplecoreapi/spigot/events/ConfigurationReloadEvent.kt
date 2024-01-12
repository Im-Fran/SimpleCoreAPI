package xyz.theprogramsrc.simplecoreapi.spigot.events

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Represents an event of when the configuration is reloaded.
 * Note: This event is fired synchronously.
 *
 * @param isAsync Whether the event is asynchronous or not. (Default to false)
 * @since SimpleCoreAPI 1.0.0
 */
open class ConfigurationReloadEvent(isAsync: Boolean = false): Event(isAsync) {

    companion object {
        private val handlers = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = handlers
    }

    override fun getHandlers(): HandlerList = Companion.handlers
}

/**
 * Represents an event of when the configuration is reloaded.
 * Note: This event is fired asynchronously.
 *
 * @since SimpleCoreAPI 1.0.0
 */
class AsyncConfigurationReloadEvent : ConfigurationReloadEvent(isAsync = true)