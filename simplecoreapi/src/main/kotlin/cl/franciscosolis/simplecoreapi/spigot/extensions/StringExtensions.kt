package cl.franciscosolis.simplecoreapi.spigot.extensions

import org.bukkit.ChatColor
import org.bukkit.Color

/**
 * Translates the color codes in the string
 * @param altColorChar The alternate color code character to use
 *
 * @return The translated string
 */
fun String.bukkitColor(altColorChar: Char = '&'): String = ChatColor.translateAlternateColorCodes(altColorChar, this)

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
fun String.bukkitStripColors(): String? = ChatColor.stripColor(this)

/**
 * Turns this hex string into a bukkit [Color]
 * @return The color
 */
fun String.bukkitToColor(): Color = Color.fromRGB(this.toInt(16))