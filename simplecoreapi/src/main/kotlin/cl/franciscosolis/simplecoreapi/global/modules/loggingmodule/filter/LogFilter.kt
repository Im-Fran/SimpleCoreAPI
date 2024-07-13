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