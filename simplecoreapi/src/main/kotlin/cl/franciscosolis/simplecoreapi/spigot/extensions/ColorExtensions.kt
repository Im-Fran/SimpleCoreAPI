package cl.franciscosolis.simplecoreapi.spigot.extensions

import org.bukkit.Color

/**
 * Turns this [Color] into a hex string
 * @return the hex string
 */
fun Color.asHex(): String = String.format("#%02x%02x%02x", this.red, this.green, this.blue)