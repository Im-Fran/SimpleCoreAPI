package cl.franciscosolis.simplecoreapi.modules

import cl.franciscosolis.simplecoreapi.modules.networking.NetworkingModule
import com.sun.net.httpserver.HttpServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.net.InetSocketAddress
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Uses the JDK's own HTTP server: no network access and no test dependencies. */
class NetworkingModuleTest {

    @TempDir
    lateinit var folder: File

    private val networking = NetworkingModule()
    private lateinit var server: HttpServer
    private lateinit var baseUrl: String

    @BeforeEach
    fun startServer() {
        server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)

        server.createContext("/hello") { exchange ->
            val body = "hello ${exchange.requestHeaders.getFirst("X-Name") ?: "world"}".toByteArray()
            exchange.responseHeaders.add("X-Test", "yes")
            exchange.sendResponseHeaders(200, body.size.toLong())
            exchange.responseBody.use { it.write(body) }
        }

        server.createContext("/echo") { exchange ->
            val received = exchange.requestBody.readBytes()
            val type = exchange.requestHeaders.getFirst("Content-Type") ?: "none"
            val body = "$type:${received.decodeToString()}".toByteArray()
            exchange.sendResponseHeaders(200, body.size.toLong())
            exchange.responseBody.use { it.write(body) }
        }

        server.createContext("/missing") { exchange ->
            exchange.sendResponseHeaders(404, -1)
            exchange.close()
        }

        server.start()
        baseUrl = "http://127.0.0.1:${server.address.port}"
    }

    @AfterEach
    fun stopServer() {
        networking.onDisable()
        server.stop(0)
    }

    @Test
    fun `get returns body headers and status code`() {
        val response = networking.get("$baseUrl/hello", mapOf("X-Name" to "fran"))

        assertTrue(response.isSuccess)
        assertEquals(200, response.code)
        assertEquals("hello fran", response.body)
        assertEquals(listOf("yes"), response.headers["x-test"])
    }

    @Test
    fun `post sends the body and postJson sets the content-type`() {
        assertEquals("none:payload", networking.post("$baseUrl/echo", "payload").body)
        assertEquals(
            """application/json:{"a":1}""",
            networking.postJson("$baseUrl/echo", """{"a":1}""").body,
        )
    }

    @Test
    fun `a 404 is not a success but does not throw`() {
        val response = networking.get("$baseUrl/missing")

        assertFalse(response.isSuccess)
        assertEquals(404, response.code)
    }

    @Test
    fun `download writes the file`() {
        val destination = networking.download("$baseUrl/hello", File(folder, "sub/download.txt"))

        assertEquals("hello world", destination.readText())
    }

    @Test
    fun `download fails without leaving a file when the response is not 2xx`() {
        val destination = File(folder, "not-written.txt")

        assertFailsWith<IllegalStateException> { networking.download("$baseUrl/missing", destination) }

        assertFalse(destination.exists())
    }
}
