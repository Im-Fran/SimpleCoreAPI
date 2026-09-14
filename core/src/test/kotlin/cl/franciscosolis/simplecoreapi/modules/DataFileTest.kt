package cl.franciscosolis.simplecoreapi.modules

import cl.franciscosolis.simplecoreapi.modules.files.DataFile
import cl.franciscosolis.simplecoreapi.modules.files.JsonFile
import cl.franciscosolis.simplecoreapi.modules.files.YamlFile
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DataFileTest {

    @TempDir
    lateinit var folder: File

    private fun create(format: String): DataFile = when (format) {
        "yaml" -> YamlFile(File(folder, "data.yml"))
        else -> JsonFile(File(folder, "data.json"))
    }

    @ParameterizedTest
    @ValueSource(strings = ["yaml", "json"])
    fun `saves and reads back nested values`(format: String) {
        val data = create(format)
        data["server.name"] = "lobby"
        data["server.slots"] = 120
        data["server.whitelist"] = true
        data["motd"] = listOf("line 1", "line 2")
        data.save()

        data.reload()

        assertEquals("lobby", data.getString("server.name"))
        assertEquals(120, data.getInt("server.slots"))
        assertTrue(data.getBoolean("server.whitelist"))
        assertEquals(listOf("line 1", "line 2"), data.getStringList("motd"))
    }

    @ParameterizedTest
    @ValueSource(strings = ["yaml", "json"])
    fun `a missing path returns the default value`(format: String) {
        val data = create(format)

        assertNull(data.getString("does.not.exist"))
        assertEquals("fallback", data.getString("does.not.exist", "fallback"))
        assertEquals(7, data.getInt("does.not.exist", 7))
        assertFalse(data.getBoolean("does.not.exist"))
        assertTrue(data.getStringList("does.not.exist").isEmpty())
        assertNull(data.section("does.not.exist"))
    }

    @ParameterizedTest
    @ValueSource(strings = ["yaml", "json"])
    fun `applyDefaults keeps the already customized values`(format: String) {
        val data = create(format)
        data["locale"] = "es"
        data.save()

        val written = data.applyDefaults(mapOf("locale" to "en", "debug" to false))

        assertTrue(written)
        assertEquals("es", data.getString("locale"))
        assertFalse(data.getBoolean("debug"))

        // Second pass: nothing is missing any more, so nothing is rewritten.
        assertFalse(data.applyDefaults(mapOf("locale" to "en", "debug" to false)))
    }

    @ParameterizedTest
    @ValueSource(strings = ["yaml", "json"])
    fun `contains remove and keys work on paths`(format: String) {
        val data = create(format)
        data["a.b.c"] = 1
        data["a.b.d"] = 2

        assertTrue("a.b.c" in data)
        assertEquals(setOf("c", "d"), data.keys("a.b"))
        assertEquals(setOf("a"), data.keys())

        assertTrue(data.remove("a.b.c"))
        assertFalse("a.b.c" in data)
        assertFalse(data.remove("a.b.c"))
    }

    @ParameterizedTest
    @ValueSource(strings = ["yaml", "json"])
    fun `an empty or missing file reads as empty`(format: String) {
        val data = create(format)
        assertTrue(data.root.isEmpty())

        data.file.writeText("")
        data.reload()

        assertTrue(data.root.isEmpty())
    }

    @Test
    fun `json numbers arrive as doubles and convert just the same`() {
        val json = JsonFile(File(folder, "n.json"))
        json.file.writeText("""{"port": 25565, "ratio": 1.5}""")
        json.reload()

        assertEquals(25565, json.getInt("port"))
        assertEquals(25565L, json.getLong("port"))
        assertEquals(1.5, json.getDouble("ratio"))
    }
}
