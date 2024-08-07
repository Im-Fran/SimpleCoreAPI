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

package cl.franciscosolis.simplecoreapi.modules.loggingmodule

import cl.franciscosolis.simplecoreapi.module.*
import cl.franciscosolis.simplecoreapi.modules.loggingmodule.filter.JavaLoggerFilter
import cl.franciscosolis.simplecoreapi.modules.loggingmodule.filter.LogFilter
import cl.franciscosolis.simplecoreapi.modules.loggingmodule.filter.LogFilterAction
import java.util.logging.Logger

class LoggingModule: Module {

    override val description: ModuleDescription = ModuleDescription(
        name = "LoggingModule",
        version = "0.4.2",
        authors = listOf("Im-Fran")
    )

    /**
     * Adds a new Log4J Filter
     * @param filterAction The action to be executed when the filter is triggered
     *
     * @return The created filter
     */
    fun addLog4JFilter(filterAction: LogFilterAction) = LogFilter(filterAction)

    /**
     * Adds a new Java Logger Filter
     * @param filterAction The action to be executed when the filter is triggered
     *
     * @return The created filter
     */
    fun addJavaLoggerFilter(logger: Logger, filterAction: LogFilterAction): JavaLoggerFilter = let {
        val filter = JavaLoggerFilter(filterAction)
        logger.filter = filter
        filter
    }

}