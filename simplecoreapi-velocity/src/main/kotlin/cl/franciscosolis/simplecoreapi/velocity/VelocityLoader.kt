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

package cl.franciscosolis.simplecoreapi.velocity

import cl.franciscosolis.simplecoreapi.SimpleCoreAPI
import com.google.inject.Inject
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.proxy.ProxyServer
import java.util.logging.Logger

@Plugin(
    id = "simplecoreapi",
    name = "SimpleCoreAPI",
    version = cl.franciscosolis.simplecoreapi.getVersion,
    url = "franciscosolis.cl",
    authors = ["GitHub/Im-Fran"]
)
class VelocityLoader @Inject constructor(val server: ProxyServer, logger: Logger) {

    companion object {
        /**
         * The instance of the [VelocityLoader]
         */
        lateinit var instance: VelocityLoader
            private set
    }

    init {
        instance = this
        SimpleCoreAPI(logger = logger)
    }
}