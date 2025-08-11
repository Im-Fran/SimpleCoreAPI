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

package cl.franciscosolis.simplecoreapi.extensions

import com.google.gson.JsonParser
import com.google.gson.Strictness
import com.google.gson.internal.Streams
import com.google.gson.stream.JsonWriter
import java.io.StringWriter
import java.util.*

/**
 * Gets the current JsonString as a pretty JsonString
 * @return the pretty JsonString
 */
fun String.prettyJson() = JsonParser.parseString(this).let {
    val stringWriter = StringWriter()
    val jsonWriter = JsonWriter(stringWriter)
    jsonWriter.setIndent("    ")
    jsonWriter.strictness = Strictness.LENIENT
    Streams.write(it, jsonWriter)
    stringWriter.toString()
}

/**
 * Turns this String into a Base64 String
 * @return the Base64 String
 */
fun String.toBase64(): String = Base64.getEncoder().encodeToString(this.toByteArray())

/**
 * Turns this Base64 String into a normal String
 * @return the normal String
 */
fun String.fromBase64(): String = String(Base64.getDecoder().decode(this))

/**
 * Capitalize this string (based on kotlin String.capitalize)
 * @return the capitalized string
 */
fun String.capitalize(): String = replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

/**
 * Applies the given placeholders to this string
 * @param placeholders The placeholders to use. Example (using placeholder id 'test' and value 'test_value'): '{test}' should return 'test_value'.
 *                      You can use '{}' or '%%' as placeholder identifiers like '{test}' or '%test%'. Defaults to an empty map.
 *
 * Example:
 * ```
 * "Hello, {name}!".applyPlaceholders(mapOf("name" to "Francisco")) // Returns "Hello, Francisco!"
 * ```
 *
 * @return the string with the placeholders applied
 */
fun String.placeholders(placeholders: Map<String, String> = emptyMap()): String {
    var newString = this
    placeholders.forEach { (key, value) ->
        newString = newString.replace("{$key}", value)
            .replace("%$key%", value)
    }
    return newString

}