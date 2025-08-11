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

package cl.franciscosolis.simplecoreapi.modules.loggingmodule.filter

import java.util.logging.Filter
import java.util.logging.LogRecord

/**
 * Representation of a LogFilter for Java Logger. This can be used to filter messages from the console.
 *
 * @param logFilterAction The function to use to filter the messages and return the result of whether to filter or not.
 */
class JavaLoggerFilter(private val logFilterAction: LogFilterAction): Filter {

    override fun isLoggable(record: LogRecord): Boolean {
        val filter = this.logFilterAction.filter(record.message)
        if(filter != null) {
            record.message = filter
        }

        return filter != null
    }
}