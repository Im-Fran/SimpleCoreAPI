package cl.franciscosolis.simplecoreapi.modules.networking

import cl.franciscosolis.simplecoreapi.module.Module
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

/**
 * HTTP requests and downloads on top of the JDK `HttpClient`. No external dependencies.
 *
 * ```kotlin
 * val net = requireModule<NetworkingModule>()
 * val response = net.get("https://api.example.com/version")
 * if (response.isSuccess) println(response.body)
 * ```
 *
 * Calls are blocking: on a Minecraft server, run them off the main thread.
 */
public class NetworkingModule : Module {

    private val lazyClient = lazy {
        HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build()
    }

    private val client: HttpClient get() = lazyClient.value

    /** Result of an HTTP request. */
    public data class Response(
        public val code: Int,
        public val body: String,
        public val headers: Map<String, List<String>>,
    ) {
        /** `true` for 2xx status codes. */
        public val isSuccess: Boolean get() = code in 200..299
    }

    public fun get(
        url: String,
        headers: Map<String, String> = emptyMap(),
        timeout: Duration = DEFAULT_TIMEOUT,
    ): Response = send(request(url, headers, timeout).GET())

    public fun post(
        url: String,
        body: String,
        headers: Map<String, String> = emptyMap(),
        timeout: Duration = DEFAULT_TIMEOUT,
    ): Response = send(request(url, headers, timeout).POST(HttpRequest.BodyPublishers.ofString(body)))

    /** Sends [body] as `application/json`. */
    public fun postJson(
        url: String,
        body: String,
        headers: Map<String, String> = emptyMap(),
        timeout: Duration = DEFAULT_TIMEOUT,
    ): Response = post(url, body, headers + ("Content-Type" to "application/json"), timeout)

    /**
     * Downloads [url] into [destination].
     *
     * @throws IllegalStateException if the response is not 2xx; nothing is written in that case.
     */
    public fun download(
        url: String,
        destination: File,
        headers: Map<String, String> = emptyMap(),
        timeout: Duration = DEFAULT_TIMEOUT,
    ): File {
        destination.parentFile?.mkdirs()
        val response = client.send(
            request(url, headers, timeout).GET().build(),
            HttpResponse.BodyHandlers.ofInputStream(),
        )
        check(response.statusCode() in 200..299) {
            "Download failed ($url): HTTP ${response.statusCode()}"
        }
        response.body().use { input ->
            destination.outputStream().buffered().use { input.copyTo(it) }
        }
        return destination
    }

    private fun request(
        url: String,
        headers: Map<String, String>,
        timeout: Duration,
    ): HttpRequest.Builder = HttpRequest.newBuilder(URI.create(url))
        .timeout(timeout)
        .header("User-Agent", USER_AGENT)
        .apply { headers.forEach { (key, value) -> header(key, value) } }

    private fun send(builder: HttpRequest.Builder): Response {
        val response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString())
        return Response(response.statusCode(), response.body(), response.headers().map())
    }

    override fun onDisable() {
        // Do not create the client just to close it.
        if (lazyClient.isInitialized()) lazyClient.value.close()
    }

    private companion object {
        private val DEFAULT_TIMEOUT: Duration = Duration.ofSeconds(30)
        private const val USER_AGENT = "SimpleCoreAPI"
    }
}
