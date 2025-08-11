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

package cl.franciscosolis.simplecoreapi.utils.color

import cl.franciscosolis.simplecoreapi.utils.text.TextColor

/**
 * Representation of Minecraft Colors
 * @param legacy The legacy color code. For example "a" is the legacy color code for light green.
 * @param hexColor The hex color code. For example "#00FF00" is the hex color code for light green.
 */
enum class MinecraftColor(
    val legacy: String,
    val hexColor: String
) {

    /**
     * Black color.
     */
    BLACK("0", "#000000"),

    /**
     * Dark blue color.
     */
    DARK_BLUE("1", "#0000AA"),

    /**
     * Dark green color.
     */
    DARK_GREEN("2", "#00AA00"),

    /**
     * Dark aqua color.
     */
    DARK_AQUA("3", "#00AAAA"),

    /**
     * Dark red color.
     */
    DARK_RED("4", "#AA0000"),

    /**
     * Dark purple color.
     */
    DARK_PURPLE("5", "#AA00AA"),

    /**
     * Gold color.
     */
    GOLD("6", "#FFAA00"),

    /**
     * Gray color.
     */
    GRAY("7", "#AAAAAA"),

    /**
     * Dark gray color.
     */
    DARK_GRAY("8", "#555555"),

    /**
     * Blue color.
     */
    BLUE("9", "#5555FF"),

    /**
     * Green color.
     */
    GREEN("a", "#55FF55"),

    /**
     * Aqua color.
     */
    AQUA("b", "#55FFFF"),

    /**
     * Red color.
     */
    RED("c", "#FF5555"),

    /**
     * Light purple color.
     */
    LIGHT_PURPLE("d", "#FF55FF"),

    /**
     * Yellow color.
     */
    YELLOW("e", "#FFFF55"),

    /**
     * White color.
     */
    WHITE("f", "#FFFFFF"),

    /**
     * Obfuscated color.
     */
    OBFUSCATED("k", "#000000"),

    /**
     * Bold color.
     */
    BOLD("l", "#000000"),

    /**
     * Strikethrough color.
     */
    STRIKETHROUGH("m", "#000000"),

    /**
     * Underline color.
     */
    UNDERLINE("n", "#000000"),

    /**
     * Italic color.
     */
    ITALIC("o", "#000000"),

    /**
     * Reset color.
     */
    RESET("r", "#000000")

    ;

    /**
     * The [TextColor] representation of this [MinecraftColor].
     */
    val textColor: TextColor by lazy { TextColor(hexColor) }

    companion object {
        /**
         * Gets the color code char
         */
        const val COLOR_CHAR: Char = '\u00A7'

        /**
         * Gets a [MinecraftColor] from the given [TextColor].
         * @param textColor The [TextColor].
         * @return The [MinecraftColor] from the given [TextColor].
         */
        fun getClosestMinecraftColor(textColor: TextColor): MinecraftColor = entries.toTypedArray().minByOrNull { it.textColor.diff(textColor) } ?: WHITE
    }
}