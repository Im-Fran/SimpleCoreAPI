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

package cl.franciscosolis.simplecoreapi.utils.text

import cl.franciscosolis.simplecoreapi.utils.color.MinecraftColor

/**
 * Representation of a text.
 * @param content The text content.
 * @param mainColor The main color of the text. (Defaults to TextColor.WHITE)
 * @param colors The colors to use in the text replacing strings. Example (using color 'TextColor.RED'): '**test**' should return '&x&F&F&5&5&5&5test'. Defaults to an empty array.
 */
open class Text(
    val content: String,
    private val mainColor: TextColor = TextColor.WHITE,
    private val colors: Array<TextColor> = emptyArray()
){

    private val colorRegex = Regex("\\*\\*(.+?)\\*\\*")

    init {
        check(colorRegex.findAll(content).count() == colors.size) { "The number of colors must match the number of colorized strings." }
    }

    /**
     * Gets the text colorized.
     * To get the text with the colors applied but not colorized, use [toString].
     *
     * @return The colorized text.
     */
    fun colorize(): String = toString().replace('&', MinecraftColor.COLOR_CHAR)

    /**
     * Gets the text with the colors applied.
     * To get the text colorized, use [colorize].
     *
     * @return The text with the colors applied.
     */
    override fun toString(): String {
        var output = "$mainColor$content"
        colors.forEach { color ->
            val string = Regex("\\*\\*(.+?)\\*\\*").findAll(content).first().groupValues[1]
            output = output.replace("**$string**", "$color$string$mainColor")
        }

        return output
    }
}