package cl.franciscosolis.simplecoreapi.global.modules.networkingmodule

import com.google.gson.JsonObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import cl.franciscosolis.simplecoreapi.global.modules.networkingmodule.models.Request
import cl.franciscosolis.simplecoreapi.global.modules.networkingmodule.models.RequestMethod
import java.net.SocketTimeoutException
import java.util.*

internal class RequestTests {

    @Test
    fun `test all request methods`() {
        RequestMethod.entries.forEach { method ->
            Thread {
                val methodTestResponse = Request("https://httpbin.org/anything")
                    .method(method)
                    .sendRequest()

                assertEquals(200, methodTestResponse.code)
                if (method.name != "HEAD" && method.name != "OPTIONS" && method.name != "PATCH") {
                    assertEquals(method.name.lowercase(), methodTestResponse.asJson()?.asJsonObject?.get("method")?.let { if(it.isJsonNull) null else it.asString }?.lowercase())
                }
            }.start()
        }
    }

    @Test
    fun `test head request`() {
        val headTestResponse = Request("https://httpbin.org/get")
            .method(RequestMethod.HEAD)
            .sendRequest()

        assertEquals(200, headTestResponse.code)
    }

    @Test
    fun `test options request`() {
        val optionsTestResponse = Request("https://httpbin.org/get")
            .method(RequestMethod.OPTIONS)
            .sendRequest()

        assertEquals(200, optionsTestResponse.code)
    }

    @Test
    fun `test patch request`() {
        val patchTestResponse = Request("https://httpbin.org/patch")
            .method(RequestMethod.PATCH)
            .sendRequest()

        assertEquals(200, patchTestResponse.code)
    }

    @Test
    fun `test request timeout of 250ms`() {
        assertThrows<SocketTimeoutException> {
            val response = Request("https://httpbin.org/delay/10")
                .timeout(250)
                .readTimeout(250)
                .get()

            assertEquals(200, response.code)
        }
    }

    @Test
    fun `test delay of 1 second`() {
        val response = Request("https://httpbin.org/delay/1")
            .get()

        assert(response.time >= 1000)
    }

    @Test
    fun `test follow 3 (no limit) redirects`() {
        val response = Request("https://httpbin.org/redirect/3")
            .get()

        assertEquals(200, response.code)
        assertEquals(3, response.redirects)
    }

    @Test
    fun `test follow 3 (limit of 2) redirects`() {
        val response = Request("https://httpbin.org/redirect/3")
            .maxRedirects(2)
            .get()

        assertEquals(302, response.code)
        assertEquals(2, response.redirects)
    }

    @Test
    fun `test args`() {
        val uuid = UUID.randomUUID().toString()
        val response = Request("https://httpbin.org/anything")
            .parameter("uuid", uuid)
            .get()

        val responseJson = response.asJson()?.asJsonObject
        assertEquals(200, response.code)
        assertEquals(uuid, responseJson?.get("args")?.asJsonObject?.get("uuid")?.asString)
    }

    @Test
    fun `test post() request with parameters`() {
        val uuid = UUID.randomUUID().toString()
        val response = Request("https://httpbin.org/post")
            .parameter("uuid", uuid)
            .post()

        val responseJson = response.asJson()?.asJsonObject
        assertNotNull(responseJson)
        assertEquals(200, response.code)
        assertEquals(uuid, responseJson?.get("args")?.asJsonObject?.get("uuid")?.asString)
    }

    @Test
    fun `test post() request with body`() {
        val uuid = UUID.randomUUID().toString()
        val response = Request("https://httpbin.org/post")
            .body("uuid=$uuid")
            .post()

        val responseJson = response.asJson()?.asJsonObject
        assertNotNull(responseJson)
        assertEquals(200, response.code)
        assertEquals("uuid=$uuid", responseJson?.get("data")?.asString)
    }

    @Test
    fun `test post() request with json body`() {
        val uuid = UUID.randomUUID().toString()
        val response = Request("https://httpbin.org/post")
            .jsonBody(JsonObject().apply { addProperty("uuid", uuid) })
            .post()

        val responseJson = response.asJson()?.asJsonObject
        assertNotNull(responseJson)
        assertEquals(200, response.code)
        assertEquals(uuid, responseJson?.get("json")?.asJsonObject?.get("uuid")?.asString)
    }

    @Test
    fun `test post() request with form`() {
        val uuid = UUID.randomUUID().toString()
        val response = Request("https://httpbin.org/put")
            .formBody (mapOf(
                "uuid" to uuid
            ))
            .put()

        val responseJson = response.asJson()?.asJsonObject
        assertNotNull(responseJson)
        assertEquals(200, response.code)
        assertEquals(uuid, responseJson?.get("form")?.asJsonObject?.get("uuid")?.asString)
    }

}