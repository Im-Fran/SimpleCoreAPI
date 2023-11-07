package xyz.theprogramsrc.simplecoreapi.global.modules.networkingmodule.models

import java.net.HttpURLConnection
import java.net.URL

/**
 * Represents a request method
 * @see RequestBuilder.method
 */
enum class RequestMethod {
    GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS
}

/**
 * Represents the protocol to use (http, https)
 * @see RequestBuilder.protocol
 */
enum class RequestProtocol {
    HTTP, HTTPS
}

/**
 * Represents a request builder
 * Example usage:
 * ```
 * RequestBuilder()
 *    .protocol(RequestProtocol.HTTPS)
 *    .host("google.com")
 *    .path("/search")
 *    .parameter("q", "Hello World")
 */
class RequestBuilder {

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
     * The timeout of the request
     * Default: -1 (No timeout)
     */
    var timeout: Int = -1

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
     * The maximum amount of redirects to follow
     * Default: -1 (No limit)
     */
    var maxRedirects: Int = -1

    /**
     * Cache the response
     * Default: false
     */
    var cacheResponse: Boolean = false

    /**
     * Constructs a new request builder
     */
    constructor(url: String) {
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

        val extra
        path = data.split("/").drop(1).joinToString("/")
    }

    /**
     * Sets the protocol of the request
     * @param protocol The protocol to use (http, https)
     * @return The request builder
     * @see RequestProtocol
     */
    fun protocol(protocol: RequestProtocol): RequestBuilder = apply {
        this.protocol = protocol
    }

    /**
     * Sets the host of the request
     * @param host The host of the server
     * @return The request builder
     */
    fun host(host: String): RequestBuilder = apply {
        this.host = host
    }

    /**
     * Sets the port of the request
     * @param port The port of the server
     * @return The request builder
     */
    fun port(port: Int): RequestBuilder = apply {
        this.port = port
    }

    /**
     * Sets the path of the request
     * @param path The path of the request
     * @return The request builder
     */
    fun path(path: String): RequestBuilder = apply {
        this.path = path
    }

    /**
     * Sets a parameter to the request, it also overrides the value if the key already exists
     * @param key The key of the parameter
     * @param value The value of the parameter
     * @return The request builder
     */
    fun parameter(key: String, value: String): RequestBuilder = apply {
        this.parameters[key] = value
    }

    /**
     * Adds a parameter to the request, it doesn't override the value if the key already exists
     * @param key The key of the parameter
     * @param value The value of the parameter
     * @return The request builder
     */
    fun addParameter(key: String, value: String): RequestBuilder = apply {
        if (!this.parameters.containsKey(key)) this.parameters[key] = value
    }

    /**
     * Sets a header to the request, it also overrides the value if the key already exists
     * @param key The key of the header
     * @param value The value of the header
     * @return The request builder
     */
    fun header(key: String, value: String): RequestBuilder = apply {
        this.headers[key] = value
    }

    /**
     * Adds a header to the request, it doesn't override the value if the key already exists
     * @param key The key of the header
     * @param value The value of the header
     * @return The request builder
     */
    fun addHeader(key: String, value: String): RequestBuilder = apply {
        if (!this.headers.containsKey(key)) this.headers[key] = value
    }

    /**
     * Sets the body of the request
     * @param body The body of the request
     * @return The request builder
     */
    fun body(body: ByteArray): RequestBuilder = apply {
        this.body = body
    }

    /**
     * Sets the timeout of the request
     * @param timeout The timeout of the request
     * @return The request builder
     */
    fun timeout(timeout: Int): RequestBuilder = apply {
        this.timeout = timeout
    }

    /**
     * Sets the request method
     * @param method The request method
     * @return The request builder
     */
    fun method(method: RequestMethod): RequestBuilder = apply {
        this.method = method
    }

    /**
     * Sets the follow redirects option
     * @param followRedirects The follow redirects option
     * @return The request builder
     */
    fun followRedirects(followRedirects: Boolean): RequestBuilder = apply {
        this.followRedirects = followRedirects
    }

    /**
     * Sets the maximum amount of redirects to follow
     * @param maxRedirects The maximum amount of redirects to follow
     * @return The request builder
     */
    fun maxRedirects(maxRedirects: Int): RequestBuilder = apply {
        this.maxRedirects = maxRedirects
    }

    /**
     * Sets the cache response option
     * @param cacheResponse The cache response option
     * @return The request builder
     */
    fun cacheResponse(cacheResponse: Boolean): RequestBuilder = apply {
        this.cacheResponse = cacheResponse
    }

    /**
     * Sends the request
     * @return The response
     */
    fun send() {
        val urlString = "${this.protocol.name.lowercase()}://${this.host}${if(this.port != null) ":${this.port}" else ""}${if(this.path != null) "/${this.path}" else ""}${if(this.parameters.isNotEmpty()) "?${this.parameters.map { "${it.key}=${it.value}" }.joinToString("&")}" else ""}"
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        this.headers.forEach { (key, value) -> connection.setRequestProperty(key, value) }

    }
}