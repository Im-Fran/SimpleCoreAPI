package xyz.theprogramsrc.simplecoreapi.global.modules.networkingmodule.models

import java.net.HttpURLConnection
import java.net.URL

/**
 * Represents a request method
 * @see Request.method
 */
enum class RequestMethod {
    GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS
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
        if(url.contains("://")) {
            protocol = RequestProtocol.valueOf(url.split("://")[0].uppercase())
        }
        val data = if(url.contains("://")) {
            url.split("://")[1]
        } else {
            url
        }
        val hostElements = data.split("/")[0]
        if(hostElements.contains(":")) {
            host = hostElements.split(":")[0]
            port = hostElements.split(":")[1].toInt()
        } else {
            host = hostElements
        }
        path = data.split("/").drop(1).joinToString("/")
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
     * Sets the body of the request
     * @param body The body of the request
     * @return The request builder
     */
    fun body(body: ByteArray): Request = apply {
        this.body = body
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
        send()
    }

    /**
     * Sets the request method to 'POST' and sends the request
     * @return The [Response] for this request
     */
    fun post(): Response = let {
        method(RequestMethod.POST)
        send()
    }

    /**
     * Sets the request method to 'PUT' and sends the request
     * @return The [Response] for this request
     */
    fun put(): Response = let {
        method(RequestMethod.PUT)
        send()
    }

    /**
     * Sets the request method to 'DELETE' and sends the request
     * @return The [Response] for this request
     */
    fun delete(): Response = let {
        method(RequestMethod.DELETE)
        send()
    }

    /**
     * Sets the request method to 'PATCH' and sends the request
     * @return The [Response] for this request
     */
    fun patch(): Response = let {
        method(RequestMethod.PATCH)
        send()
    }

    /**
     * Sets the request method to 'HEAD' and sends the request
     * @return The [Response] for this request
     */
    fun head(): Response = let {
        method(RequestMethod.HEAD)
        send()
    }

    /**
     * Sets the request method to 'OPTIONS' and sends the request
     * @return The [Response] for this request
     */
    fun options(): Response = let {
        method(RequestMethod.OPTIONS)
        send()
    }

    /**
     * Sends the request
     * @return The [Response] for this request
     */
    fun send(): Response {
        fun connect(urlString: String): Response {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            this.headers.forEach { (key, value) -> connection.setRequestProperty(key, value) }
            connection.requestMethod = this.method.name
            connection.connectTimeout = this.timeout
            connection.readTimeout = this.readTimeout
            if(this.body != null) {
                connection.doOutput = true
                connection.outputStream.write(this.body!!)
            }
            connection.doInput = true

            val responseCode = connection.responseCode
            val responseMessage = connection.responseMessage
            val headers = connection.headerFields
            val cookies = connection.headerFields["Set-Cookie"]?.map { it.split(";")[0] }?.map { it.split("=") }?.associate { it[0] to it[1] } ?: emptyMap()
            var error: Exception? = null

            val responseBody = try {
                connection.inputStream.use {
                    it.readAllBytes()
                }
            } catch (e: Exception) {
                error = e
                null
            }

            connection.disconnect()

            return Response(
                request = this,
                code = responseCode,
                message = responseMessage,
                responseBody = responseBody,
                headers = headers,
                cookies = cookies,
                error = error
            )
        }


        var response = connect("${this.protocol.name.lowercase()}://${this.host}${if(this.port != null) ":${this.port}" else ""}${if(this.path != null) "/${this.path}" else ""}${if(this.parameters.isNotEmpty()) "?${this.parameters.map { "${it.key}=${it.value}" }.joinToString("&")}" else ""}")

        if (!this.followRedirects || (this.followRedirects && this.maxRedirects == 0)) {
            return response
        }

        var redirects = 0
        while (response.code in 300..399 && (this.maxRedirects == -1 || redirects < this.maxRedirects)) {
            val location = response.headers["Location"]?.firstOrNull() ?: break
            response = connect(location)
            redirects++
        }

        return response
    }
}