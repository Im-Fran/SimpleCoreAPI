package cl.franciscosolis.simplecoreapi.modules

import cl.franciscosolis.simplecoreapi.modules.compression.CompressionModule
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

class CompressionModuleTest {

    @TempDir
    lateinit var folder: File

    private val compression = CompressionModule()

    @Test
    fun `zipping and unzipping a folder round-trips the whole tree`() {
        val source = File(folder, "source").apply { mkdirs() }
        File(source, "config.yml").writeText("debug: true")
        File(source, "lang/en.yml").apply { parentFile.mkdirs() }.writeText("hello: world")

        val zip = compression.zip(source, File(folder, "out/backup.zip"))
        val restored = compression.unzip(zip, File(folder, "restored"))

        assertEquals("debug: true", File(restored, "config.yml").readText())
        assertEquals("hello: world", File(restored, "lang/en.yml").readText())
        assertEquals(2, restored.walkTopDown().filter { it.isFile }.count())
    }

    @Test
    fun `zipping a single file`() {
        val source = File(folder, "single.txt").apply { writeText("contents") }

        val restored = compression.unzip(
            compression.zip(source, File(folder, "single.zip")),
            File(folder, "out"),
        )

        assertEquals("contents", File(restored, "single.txt").readText())
    }

    @Test
    fun `unzip rejects entries escaping the destination`() {
        val malicious = File(folder, "evil.zip")
        ZipOutputStream(malicious.outputStream()).use {
            it.putNextEntry(ZipEntry("../escaped.txt"))
            it.write("pwned".toByteArray())
            it.closeEntry()
        }

        val error = assertFailsWith<IllegalArgumentException> {
            compression.unzip(malicious, File(folder, "destination"))
        }

        assertContains(error.message!!, "zip slip")
        assertFalse(File(folder, "escaped.txt").exists())
    }

    @Test
    fun `gzip and gunzip of bytes are symmetric`() {
        val original = "repeated text ".repeat(100).toByteArray()

        val compressed = compression.gzip(original)

        assertEquals(original.toList(), compression.gunzip(compressed).toList())
        assert(compressed.size < original.size)
    }

    @Test
    fun `gzip and gunzip of files are symmetric`() {
        val source = File(folder, "latest.log").apply { writeText("line\n".repeat(200)) }

        compression.gzip(source, File(folder, "latest.log.gz"))
        compression.gunzip(File(folder, "latest.log.gz"), File(folder, "restored.log"))

        assertEquals(source.readText(), File(folder, "restored.log").readText())
    }
}
