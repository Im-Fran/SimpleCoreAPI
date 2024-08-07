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

package cl.franciscosolis.simplecoreapi.modules.networkingmodule.models

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser

/**
 * Representation of an HTTP Response
 * @param request The [Request] that generated this response
 * @param code The response code
 * @param message The response message
 * @param responseBody The response body
 * @param headers The response headers
 * @param cookies The response cookies
 * @param error The error if any (null if no error)
 * @param time The time it took to get the response (in milliseconds)
 * @param redirects The number of redirects
 */
data class Response(
    val request: Request,
    val code: Int,
    val message: String,
    val responseBody: ByteArray?,
    val headers: Map<String, List<String>>,
    val cookies: Map<String, String>,
    val error: Exception?,
    val time: Long,
    val redirects: Int
) {

    /**
     * Get the response body as a String
     * @return The response body as a String
     */
    fun asString(): String? = this.responseBody?.let { String(it) }

    /**
     * Get the response body as a JsonElement
     * @return The response body as a JsonElement
     */
    fun asJson(): JsonElement? = this.responseBody?.let {
        try {
            JsonParser.parseString(String(it))
        }catch (e: Exception){
            null
        }
    }

    /**
     * Converts this response to a JSON String representation
     * @return The JSON String representation of this response
     */
    override fun toString(): String = JsonObject().apply {
        add("request", JsonParser.parseString(request.toString()))
        addProperty("code", code)
        addProperty("message", message)
        if(headers["Content-Type"]?.firstOrNull()?.contains("application/json") == true) {
            add("body", asJson())
        } else {
            addProperty("body", asString())
        }
        val headers = JsonObject()
        this@Response.headers.forEach { (key, value) ->
            val values = JsonArray()
            value.forEach { values.add(it) }
            headers.add(key, values)
        }
        add("headers", headers)
        val cookies = JsonObject()
        this@Response.cookies.forEach { (key, value) -> cookies.addProperty(key, value) }
        add("cookies", cookies)
        addProperty("error", error?.stackTraceToString())
        addProperty("time", time)
        addProperty("redirects", redirects)
    }.toString()

    /**
     * Checks if this response is equal to another object
     * @param other The other object
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Response

        if (request != other.request) return false
        if (code != other.code) return false
        if (message != other.message) return false
        if (responseBody != null) {
            if (other.responseBody == null) return false
            if (!responseBody.contentEquals(other.responseBody)) return false
        } else if (other.responseBody != null) return false
        if (headers != other.headers) return false
        if (cookies != other.cookies) return false
        if (error != other.error) return false
        if (time != other.time) return false
        if (redirects != other.redirects) return false

        return true
    }

    /**
     * Gets the hash code of this response
     * @return The hash code of this response
     */
    override fun hashCode(): Int {
        var result = request.hashCode()
        result = 31 * result + code
        result = 31 * result + message.hashCode()
        result = 31 * result + (responseBody?.contentHashCode() ?: 0)
        result = 31 * result + headers.hashCode()
        result = 31 * result + cookies.hashCode()
        result = 31 * result + (error?.hashCode() ?: 0)
        result = 31 * result + time.hashCode()
        result = 31 * result + redirects
        return result
    }

}
