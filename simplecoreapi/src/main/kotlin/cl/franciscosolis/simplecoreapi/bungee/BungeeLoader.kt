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

package cl.franciscosolis.simplecoreapi.bungee

import net.md_5.bungee.api.plugin.Plugin
import cl.franciscosolis.simplecoreapi.global.SimpleCoreAPI

class BungeeLoader: Plugin() {

    companion object {
        lateinit var instance: BungeeLoader
            private set
    }

    override fun onLoad() {
        instance = this
        SimpleCoreAPI()
    }

}