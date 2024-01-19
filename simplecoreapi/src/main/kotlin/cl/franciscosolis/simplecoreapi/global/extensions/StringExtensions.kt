package cl.franciscosolis.simplecoreapi.global.extensions

import com.google.gson.JsonParser
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
    jsonWriter.isLenient = true
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