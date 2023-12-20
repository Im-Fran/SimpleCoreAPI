package xyz.theprogramsrc.simplecoreapi.global.extensions

import com.google.gson.JsonParser
import com.google.gson.internal.Streams
import com.google.gson.stream.JsonWriter
import java.io.StringWriter

fun String.prettyJson() = JsonParser.parseString(this).let {
    val stringWriter = StringWriter()
    val jsonWriter = JsonWriter(stringWriter)
    jsonWriter.setIndent("    ")
    jsonWriter.isLenient = true
    Streams.write(it, jsonWriter)
    stringWriter.toString()
}