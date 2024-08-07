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

package cl.franciscosolis.simplecoreapi.bukkit.extensions

import org.bukkit.Color
import java.util.regex.Pattern

/**
 * Translates the color codes in the string
 * @param altColorChar The alternate color code character to use
 *
 * @return The translated string
 */
fun String.bukkitColor(altColorChar: Char = '&'): String = this.replace(altColorChar, '\u00A7')

/**
 * Translates the color codes in the string and formats it
 * @param args The arguments to format the string with
 * @return The translated and formatted string
 */
fun String.bukkitColorFormat(vararg args: Any): String = String.format(this.bukkitColor(), *args)

/**
 * Translates the color codes in the string and formats it
 * @param args The arguments to format the string with
 * @param altColorChar The alternate color code character to use
 * @return The translated and formatted string
 */
fun String.bukkitColorFormat(altColorChar: Char = '&', vararg args: Any): String = String.format(this.bukkitColor(altColorChar), *args)

/**
 * Strips the colors from the string
 * @return The stripped string
 */
fun String.bukkitStripColors(): String? = Pattern.compile("(?i)" + '\u00A7' + "[0-9A-FK-ORX]").matcher(this).replaceAll("")

/**
 * Turns this hex string into a bukkit [Color]
 * @return The color
 */
fun String.bukkitToColor(): Color = Color.fromRGB(this.toInt(16))