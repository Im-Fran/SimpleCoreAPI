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

package cl.franciscosolis.simplecoreapi.modules.networkingmodule

import cl.franciscosolis.simplecoreapi.module.Module
import cl.franciscosolis.simplecoreapi.module.ModuleDescription
import cl.franciscosolis.simplecoreapi.modules.networkingmodule.models.Request

class NetworkingModule: Module {
    override val description: ModuleDescription = ModuleDescription(
        name = "NetworkingModule",
        version = "0.1.0",
        authors = listOf("Im-Fran")
    )

    /**
     * Creates a new request
     * @param url The URL to send the request
     * @returns A new request
     */
    fun request(url: String) = Request(url = url)
}