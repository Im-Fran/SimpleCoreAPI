package cl.franciscosolis.simplecoreapi.global.modules.loggingmodule.filter

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.core.Filter
import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.Logger
import org.apache.logging.log4j.core.filter.AbstractFilter
import org.apache.logging.log4j.message.Message

/**
 * Representation of a LogFilter. This can be used to filter messages from the console.
 *
 * @param filter The function to use to filter the messages and return the result of whether to filter or not.
 */
class LogFilter(private val filter: (String) -> FilterResult): AbstractFilter() {

    companion object {
        private val logger = LogManager.getRootLogger() as Logger
    }

    init {
        logger.addFilter(this)
    }

    private fun process(message: String?): Filter.Result {
        if (message == null) {
            return Filter.Result.NEUTRAL
        }

        return Filter.Result.valueOf(filter(message).name)
    }

    override fun filter(event: LogEvent?): Filter.Result = process(event?.message?.formattedMessage)

    override fun filter(logger: Logger?, level: Level?, marker: Marker?, msg: Message, t: Throwable?): Filter.Result = process(msg.formattedMessage)

    override fun filter(logger: Logger?, level: Level?, marker: Marker?, msg: Any, t: Throwable?): Filter.Result = process(msg.toString())

    override fun filter(logger: Logger?, level: Level?, marker: Marker?, msg: String?, vararg params: Any?): Filter.Result = process(msg)

}