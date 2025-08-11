package cl.franciscosolis.simplecoreapi.paper.extensions

import cl.franciscosolis.simplecoreapi.utils.text.Text
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

/**
 * Gets this [Text] as a [TextComponent].
 * @return The [TextComponent] representation of this [Text].
 */
fun Text.asComponent(colorize: Boolean = true): TextComponent = LegacyComponentSerializer.legacySection().deserialize(if(colorize) colorize() else toString())