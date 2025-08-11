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

import cl.franciscosolis.simplecoreapi.utils.color.MinecraftColor.entries
import kotlin.math.abs

/**
 * Representation of a Text Color
 * @param hexColor The hex color code. For example "#00FF00" is the hex color code for light green.
 */
class TextColor(val hexColor: String) {

    /**
     * The red value of the color.
     * @return The red value of the color.
     */
    val r = Integer.parseInt(hexColor.substring(1, 3), 16)

    /**
     * The green value of the color.
     * @return The green value of the color.
     */
    val g = Integer.parseInt(hexColor.substring(3, 5), 16)

    /**
     * The blue value of the color.
     * @return The blue value of the color.
     */
    val b = Integer.parseInt(hexColor.substring(5, 7), 16);

    /**
     * Checks the difference between this [TextColor] and another [TextColor].
     * @param other The other [TextColor].
     * @return The difference between this [TextColor] and the other [TextColor].
     */
    fun diff(other: TextColor): Int = abs(r - other.r) + abs(g - other.g) + abs(b - other.b)

    /**
     * Converts this [TextColor] to a string by
     * joining the hex color code with "&" before each character.
     * Example: #5271FF converts to &x&5&2&7&1&F&F
     *
     * @return The string representation of this [TextColor].
     */
    override fun toString(): String = "&x".plus(hexColor.replace("#","").toCharArray().joinToString(""){ "&$it" })

    companion object {

        /**
         * Creates a [TextColor] from the given RGB values.
         * @param r The red value.
         * @param g The green value.
         * @param b The blue value.
         * @return The [TextColor] from the given RGB values.
         */
        fun fromRGB(r: Int, g: Int, b: Int): TextColor =
            TextColor("#${r.toString(16)}${g.toString(16)}${b.toString(16)}")

        /**
         * Creates a [TextColor] from the given color code.
         * @param colorCode The color code.
         * @return The [TextColor] from the given color code.
         */
        fun fromColorCode(colorCode: String): TextColor = entries.first { it.legacy == colorCode }.textColor

        /* Default Colors */

        /**
         * Black color.
         */
        val BLACK = TextColor("#000000")

        /**
         * Dark blue color.
         */
        val DARK_BLUE = TextColor("#0000AA")

        /**
         * Dark green color.
         */
        val DARK_GREEN = TextColor("#00AA00")

        /**
         * Dark aqua color.
         */
        val DARK_AQUA = TextColor("#00AAAA")

        /**
         * Dark red color.
         */
        val DARK_RED = TextColor("#AA0000")

        /**
         * Dark purple color.
         */
        val DARK_PURPLE = TextColor("#AA00AA")

        /**
         * Gold color.
         */
        val GOLD = TextColor("#FFAA00")

        /**
         * Gray color.
         */
        val GRAY = TextColor("#AAAAAA")

        /**
         * Dark gray color.
         */
        val DARK_GRAY = TextColor("#555555")

        /**
         * Blue color.
         */
        val BLUE = TextColor("#5555FF")

        /**
         * Green color.
         */
        val GREEN = TextColor("#55FF55")

        /**
         * Aqua color.
         */
        val AQUA = TextColor("#55FFFF")

        /**
         * Red color.
         */
        val RED = TextColor("#FF5555")

        /**
         * Light purple color.
         */
        val LIGHT_PURPLE = TextColor("#FF55FF")

        /**
         * Yellow color.
         */
        val YELLOW = TextColor("#FFFF55")

        /**
         * White color.
         */
        val WHITE = TextColor("#FFFFFF")
    }

}