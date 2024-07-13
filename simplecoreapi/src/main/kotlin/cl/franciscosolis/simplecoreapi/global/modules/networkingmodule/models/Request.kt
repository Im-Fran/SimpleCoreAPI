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

package cl.franciscosolis.simplecoreapi.global.modules.networkingmodule.models

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import cl.franciscosolis.simplecoreapi.global.SimpleCoreAPI
import cl.franciscosolis.simplecoreapi.global.extensions.debug
import cl.franciscosolis.simplecoreapi.global.extensions.prettyJson
import java.net.HttpURLConnection
import java.net.URI
import java.util.concurrent.atomic.AtomicInteger

/**
 * Represents a request method
 * @see Request.method
 */
enum class RequestMethod(
    val applyMethod: HttpURLConnection.(Request) -> Unit
) {
    GET(applyMethod = {
        requestMethod = "GET"
    }),
    POST(applyMethod = {
        requestMethod = "POST"
    }),
    PUT(applyMethod = {
        requestMethod = "PUT"
    }),
    DELETE(applyMethod = {
        requestMethod = "DELETE"
    }),
    PATCH(applyMethod = {
        requestMethod = "OPTIONS"
        it.header("X-HTTP-Method-Override", "PATCH")
    }),
    HEAD(applyMethod = {
        requestMethod = "HEAD"
    }),
    OPTIONS(applyMethod = {
        requestMethod = "OPTIONS"
    }),
}

/**
 * Represents the protocol to use (http, https)
 * @see Request.protocol
 */
enum class RequestProtocol {
    HTTP, HTTPS
}

/**
 * Represents a request builder
 * Example usage:
 * ```kt
 * Request()
 *    .protocol(RequestProtocol.HTTPS)
 *    .host("google.com")
 *    .path("/search")
 *    .parameter("q", "Hello World")
 *    .send()
 * ```
 *
 * or
 * ```kt
 * Request("https://google.com/search?q=Hello%20World")
 *    .send()
 * ```
 */
class Request(url: String) {

    /**
     * The protocol to use (http, https)
     * Default: https
     */
    var protocol: RequestProtocol = RequestProtocol.HTTPS

    /**
     * The host of the server
     */
    var host: String = ""

    /**
     * The port of the server
     * Default: null (No port)
     */
    var port: Int? = null

    /**
     * The path of the request
     * Default: null (No path)
     */
    var path: String? = null

    /**
     * The parameters of the request
     * Default: Empty map
     */
    var parameters: MutableMap<String, String> = mutableMapOf()

    /**
     * The headers of the request
     * Default: User-Agent, Accept
     */
    var headers: MutableMap<String, String> = mutableMapOf(
        "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36",
        "Accept" to "*/*",
        "Content-Type" to "text/plain",
    )

    /**
     * The body of the request
     * Default: null (No body is sent)
     */
    var body: ByteArray? = null

    /**
     * The timeout of the request in millisecond
     * Default: 0 (No timeout)
     */
    var timeout: Int = 0

    /**
     * The timeout for reading the response in millisecond
     * Default: 0 (No timeout)
     */
    var readTimeout: Int = 0

    /**
     * The request method
     * Default: GET
     */
    var method: RequestMethod = RequestMethod.GET

    /**
     * Follow redirects
     * Default: true
     */
    var followRedirects: Boolean = true

    /**
     * The maximum number of redirects to follow
     * Default: -1
     *
     * Note: This only works if followRedirects is set to true
     * - -1 = No limit
     * - 0 = No redirects
     * - 1 = 1 redirect
     * - n = n redirects
     * @see followRedirects
     */
    var maxRedirects: Int = -1

    init {
        if (url.contains("://")) {
            protocol = RequestProtocol.valueOf(url.split("://")[0].uppercase())
        }
        val data = if (url.contains("://")) {
            url.split("://")[1]
        } else {
            url
        }
        val hostElements = data.split("/")[0]
        if (hostElements.contains(":")) {
            host = hostElements.split(":")[0]
            port = hostElements.split(":")[1].toInt()
        } else {
            host = hostElements
        }
        path = data.split("/").drop(1).joinToString("/").substringBefore("?")
        (data.split("?").drop(1).firstOrNull() ?: "").split("&").forEach { parameter ->
            val key = parameter.split("=")[0]
            val value = parameter.split("=").drop(1).joinToString("=")
            if (key.isNotBlank() && value.isNotBlank()) {
                parameters[key] = value
            }
        }
    }

    /**
     * Sets the protocol of the request
     * @param protocol The protocol to use (http, https)
     * @return The request builder
     * @see RequestProtocol
     */
    fun protocol(protocol: RequestProtocol): Request = apply {
        this.protocol = protocol
    }

    /**
     * Sets the host of the request
     * @param host The host of the server
     * @return The request builder
     */
    fun host(host: String): Request = apply {
        this.host = host
    }

    /**
     * Sets the port of the request
     * @param port The port of the server
     * @return The request builder
     */
    fun port(port: Int): Request = apply {
        this.port = port
    }

    /**
     * Sets the path of the request
     * @param path The path of the request
     * @return The request builder
     */
    fun path(path: String): Request = apply {
        this.path = path
    }

    /**
     * Sets a parameter to the request, it also overrides the value if the key already exists
     * @param key The key of the parameter
     * @param value The value of the parameter
     * @return The request builder
     */
    fun parameter(key: String, value: String): Request = apply {
        this.parameters[key] = value
    }

    /**
     * Adds a parameter to the request, it doesn't override the value if the key already exists
     * @param key The key of the parameter
     * @param value The value of the parameter
     * @return The request builder
     */
    fun addParameter(key: String, value: String): Request = apply {
        if (!this.parameters.containsKey(key)) this.parameters[key] = value
    }

    /**
     * Sets the given parameters to the request, it also overrides the value if the key already exists
     * @param parameters The parameters to set
     * @return The request builder
     */
    fun parameters(parameters: Map<String, String>): Request = apply {
        this.parameters.putAll(parameters)
    }

    /**
     * Adds the given parameters to the request, it doesn't override the value if the key already exists
     * @param parameters The parameters to add
     * @return The request builder
     */
    fun addParameters(parameters: Map<String, String>): Request = apply {
        parameters.forEach { (key, value) -> if (!this.parameters.containsKey(key)) this.parameters[key] = value }
    }

    /**
     * Sets a header to the request, it also overrides the value if the key already exists
     * @param key The key of the header
     * @param value The value of the header
     * @return The request builder
     */
    fun header(key: String, value: String): Request = apply {
        this.headers[key] = value
    }

    /**
     * Adds a header to the request, it doesn't override the value if the key already exists
     * @param key The key of the header
     * @param value The value of the header
     * @return The request builder
     */
    fun addHeader(key: String, value: String): Request = apply {
        if (!this.headers.containsKey(key)) this.headers[key] = value
    }

    /**
     * Sets the given headers to the request, it also overrides the value if the key already exists
     * @param headers The headers to set
     * @return The request builder
     */
    fun headers(headers: Map<String, String>): Request = apply {
        this.headers.putAll(headers)
    }

    /**
     * Adds the given headers to the request, it doesn't override the value if the key already exists
     * @param headers The headers to add
     * @return The request builder
     */
    fun addHeaders(headers: Map<String, String>): Request = apply {
        headers.forEach { (key, value) -> if (!this.headers.containsKey(key)) this.headers[key] = value }
    }

    /**
     * Sets the body of the request
     * @param body The body of the request
     * @return The request builder
     */
    fun body(body: ByteArray): Request = apply {
        this.body = body
    }

    /**
     * Sets the body of the request
     * @param body The body of the request as string
     */
    fun body(body: String): Request = body(body.toByteArray())

    /**
     * Sets the json body of the request
     * @param body The body of the request as json
     */
    fun jsonBody(body: JsonElement): Request = apply {
        this.body = body.toString().toByteArray()
        this.headers["Content-Type"] = "application/json"
    }

    /**
     * Sets the form body of the request
     * @param body A map of the form body
     */
    fun formBody(body: Map<String, String>): Request = apply {
        this.body = body.map { "${it.key}=${it.value}" }.joinToString("&").toByteArray()
        this.headers["Content-Type"] = "application/x-www-form-urlencoded"
    }

    /**
     * Sets the timeout of the request
     * @param timeout The timeout of the request
     * @return The request builder
     */
    fun timeout(timeout: Int): Request = apply {
        this.timeout = timeout
    }

    /**
     * Sets the read timeout of the request
     * @param readTimeout The read timeout of the request
     * @return The request builder
     */
    fun readTimeout(readTimeout: Int): Request = apply {
        this.readTimeout = readTimeout
    }

    /**
     * Sets the request method
     * @param method The request method
     * @return The request builder
     */
    fun method(method: RequestMethod): Request = apply {
        this.method = method
    }

    /**
     * Sets the follow redirects option
     * @param followRedirects The follow redirects option
     * @return The request builder
     */
    fun followRedirects(followRedirects: Boolean): Request = apply {
        this.followRedirects = followRedirects
    }

    /**
     * Sets the maximum amount of redirects to follow
     * @param maxRedirects The maximum amount of redirects to follow
     * @return The request builder
     */
    fun maxRedirects(maxRedirects: Int): Request = apply {
        this.maxRedirects = maxRedirects
    }

    /**
     * Sets the request method to 'GET' and sends the request
     * @return The [Response] for this request
     */
    fun get(): Response = let {
        method(RequestMethod.GET)
        sendRequest()
    }

    /**
     * Sets the request method to 'POST' and sends the request
     * @return The [Response] for this request
     */
    fun post(): Response = let {
        method(RequestMethod.POST)
        sendRequest()
    }

    /**
     * Sets the request method to 'PUT' and sends the request
     * @return The [Response] for this request
     */
    fun put(): Response = let {
        method(RequestMethod.PUT)
        sendRequest()
    }

    /**
     * Sets the request method to 'DELETE' and sends the request
     * @return The [Response] for this request
     */
    fun delete(): Response = let {
        method(RequestMethod.DELETE)
        sendRequest()
    }

    /**
     * Sets the request method to 'PATCH' and sends the request
     * @return The [Response] for this request
     */
    fun patch(): Response = let {
        method(RequestMethod.PATCH)
        sendRequest()
    }

    /**
     * Sets the request method to 'HEAD' and sends the request
     * @return The [Response] for this request
     */
    fun head(): Response = let {
        method(RequestMethod.HEAD)
        sendRequest()
    }

    /**
     * Sets the request method to 'OPTIONS' and sends the request
     * @return The [Response] for this request
     */
    fun options(): Response = let {
        method(RequestMethod.OPTIONS)
        sendRequest()
    }

    /**
     * Turns this request information (protocol, host, port, path and params) into a url string
     * @return The url string
     */
    fun toUrlString(): String =
        "${this.protocol.name.lowercase()}://${this.host}${if (this.port != null) ":${this.port}" else ""}${if (this.path != null) "/${this.path}" else ""}${
            if (this.parameters.isNotEmpty()) "?${
                this.parameters.filter { it.key.isNotBlank() && it.value.isNotBlank() }.map { "${it.key}=${it.value}" }
                    .joinToString("&")
            }" else ""
        }"

    /**
     * Sends the request
     * @return The [Response] for this request
     */
    fun sendRequest(): Response =
        RequestProcessor(this).process()

    override fun toString(): String = JsonObject().apply {
        addProperty("protocol", protocol.name)
        addProperty("host", host)
        addProperty("port", port)
        addProperty("path", path)

        val parameters = JsonObject()
        this@Request.parameters.forEach { (key, value) -> parameters.addProperty(key, value) }
        add("parameters", parameters)

        val headers = JsonObject()
        this@Request.headers.forEach { (key, value) -> headers.addProperty(key, value) }
        add("headers", headers)

        addProperty("body", body?.let { String(it) })
        addProperty("timeout", timeout)
        addProperty("read_timeout", readTimeout)
        addProperty("method", method.name)
        addProperty("follow_redirects", followRedirects)
        addProperty("max_redirects", maxRedirects)
        addProperty("url_string", toUrlString())
    }.toString()

    /**
     * Checks if this request is equal to another request
     * @param other The other request
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Request

        if (protocol != other.protocol) return false
        if (host != other.host) return false
        if (port != other.port) return false
        if (path != other.path) return false
        if (parameters != other.parameters) return false
        if (headers != other.headers) return false
        if (body != null) {
            if (other.body == null) return false
            if (!body.contentEquals(other.body)) return false
        } else if (other.body != null) return false
        if (timeout != other.timeout) return false
        if (readTimeout != other.readTimeout) return false
        if (method != other.method) return false
        if (followRedirects != other.followRedirects) return false
        if (maxRedirects != other.maxRedirects) return false

        return true
    }

    /**
     * Returns the hash code of this request
     * @return The hash code of this request
     */
    override fun hashCode(): Int {
        var result = protocol.hashCode()
        result = 31 * result + host.hashCode()
        result = 31 * result + (port ?: 0)
        result = 31 * result + (path?.hashCode() ?: 0)
        result = 31 * result + parameters.hashCode()
        result = 31 * result + headers.hashCode()
        result = 31 * result + (body?.contentHashCode() ?: 0)
        result = 31 * result + timeout
        result = 31 * result + readTimeout
        result = 31 * result + method.hashCode()
        result = 31 * result + followRedirects.hashCode()
        result = 31 * result + maxRedirects
        return result
    }


}

private class RequestProcessor(private val initialRequest: Request) {

    private val redirects = AtomicInteger(0)

    fun process(): Response = process(initialRequest)

    fun process(request: Request): Response {
        val startTime = System.currentTimeMillis()
        val url = URI.create(request.toUrlString()).toURL()
        val connection = url.openConnection() as HttpURLConnection
        connection.instanceFollowRedirects = false // We will manually handle this to offer limited redirects and more control
        request.method.applyMethod(connection, request)
        request.headers.forEach { (key, value) -> connection.setRequestProperty(key, value) }
        connection.connectTimeout = request.timeout
        connection.readTimeout = request.readTimeout
        connection.doInput = true
        if (request.body != null) {
            connection.doOutput = true
            connection.outputStream.write(request.body!!)
        }

        val responseCode = connection.responseCode
        val responseMessage = connection.responseMessage
        val headers = connection.headerFields.filter { it.key != null && it.value != null }
        val cookies = headers["Set-Cookie"]?.map { it.split(";")[0] }?.map { it.split("=") }?.associate { it[0] to it[1] } ?: emptyMap()
        var error: Exception? = null

        val responseBody = try { connection.inputStream.use {
            it.readAllBytes()
        }
        } catch (e: Exception) {
            error = e
            null
        }

        connection.disconnect()

        val response = Response(
            request = request,
            code = responseCode,
            message = responseMessage,
            responseBody = responseBody,
            headers = headers,
            cookies = cookies,
            error = error,
            time = System.currentTimeMillis() - startTime,
            redirects = redirects.get()
        ).also {
            SimpleCoreAPI.logger.debug("[HTTP -> ${request.method.name} ${request.toUrlString()}]: ${it.code} ${it.message} (${it.time}ms)\n${it.toString().prettyJson()}")
        }
        if (!initialRequest.followRedirects || (initialRequest.maxRedirects != -1 && redirects.get() >= initialRequest.maxRedirects)) {
            return response
        }

        if ((response.code in 300..399 || response.headers.containsKey("Location")) && (initialRequest.maxRedirects == -1 || redirects.get() < initialRequest.maxRedirects)) {
            val location = response.headers["Location"]?.firstOrNull() ?: return response
            val uri = if (!location.startsWith("http:/") && !location.startsWith("https:/")) { // treat as a relative path
                "${response.request.protocol.name.lowercase()}://${response.request.host}${if (response.request.port != null) ":${response.request.port}" else ""}${if (location.startsWith("/")) location else "/$location"}"
            } else {
                location
            }
            redirects.incrementAndGet()
            return process(
                Request(uri)
                    .protocol(response.request.protocol)
                    .parameters(response.request.parameters)
                    .headers(response.request.headers)
                    .apply {
                        if (response.request.body != null) {
                            body(response.request.body!!)
                        }
                    }
                    .timeout(response.request.timeout)
                    .readTimeout(response.request.readTimeout)
                    .method(response.request.method)
                    .followRedirects(response.request.followRedirects)
                    .maxRedirects(initialRequest.maxRedirects)
            )
        }

        return response
    }
}