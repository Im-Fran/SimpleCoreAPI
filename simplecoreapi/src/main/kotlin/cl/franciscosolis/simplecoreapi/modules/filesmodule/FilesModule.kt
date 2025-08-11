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

package cl.franciscosolis.simplecoreapi.modules.filesmodule

import cl.franciscosolis.simplecoreapi.module.*
import cl.franciscosolis.simplecoreapi.modules.filesmodule.config.JsonConfig
import cl.franciscosolis.simplecoreapi.modules.filesmodule.config.PropertiesConfig
import cl.franciscosolis.simplecoreapi.modules.filesmodule.config.YmlConfig
import java.io.File

class FilesModule: Module {

    override val description: ModuleDescription = ModuleDescription(
        name = "FilesModule",
        version = "0.4.0",
        authors = listOf("Im-Fran")
    )

    /**
     * Creates a new .json configuration
     * @param file The file where the configuration will be stored
     * @return A new [JsonConfig]
     */
    fun json(file: File): JsonConfig = JsonConfig(file = file)

    /**
     * Creates a new .yml configuration
     * @param file The file where the configuration will be stored
     * @return A new [YmlConfig]
     */
    fun yml(file: File): YmlConfig = YmlConfig(file = file)

    /**
     * Creates a new .properties configuration
     * @param file The file where the configuration will be stored
     * @return A new [PropertiesConfig]
     */
    fun properties(file: File): PropertiesConfig = PropertiesConfig(file = file)
}