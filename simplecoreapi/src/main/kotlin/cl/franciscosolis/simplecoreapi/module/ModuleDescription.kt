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

package cl.franciscosolis.simplecoreapi.module

data class ModuleDescription(
    /**
     * The name of the module.
     */
    val name: String,

    /**
     * The version of the module.
     */
    val version: String,

    /**
     * The authors of the module.
     * Use the following format: `<site>/<username>`
     *
     * Example:
     * - GitHub: `GitHub/Im-Fran`
     * - SpigotMC: `SpigotMC/fran.dev_`
     */
    val authors: List<String>,
)