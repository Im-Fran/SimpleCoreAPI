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

package cl.franciscosolis.simplecoreapi.paper.extensions

import org.bukkit.Color

/**
 * Turns this [Color] into a hex string
 * @return the hex string
 */
fun Color.asHex(): String = String.format("#%02x%02x%02x", this.red, this.green, this.blue)

/**
 * Turns a Hex string into a [Color]
 * @return the color
 */
fun String.hexToColor(): Color = let {
    check(it.startsWith("#")) { "The color must start with a '#'." }
    check(it.length == 7) { "The color must have 7 characters." }
    check(it.substring(1).all { c -> c.isDigit() || c in 'a'..'f' }) { "The color must be a valid hex color." }
    check(it.substring(1).substring(0, 2).toInt(16) in 0..255) { "The red value must be between 0 and 255." }
    check(it.substring(3, 5).toInt(16) in 0..255) { "The green value must be between 0 and 255." }
    check(it.substring(5, 7).toInt(16) in 0..255) { "The blue value must be between 0 and 255." }

    Color.fromRGB(
        it.substring(1, 3).toInt(16),
        it.substring(3, 5).toInt(16),
        it.substring(5, 7).toInt(16)
    )
}