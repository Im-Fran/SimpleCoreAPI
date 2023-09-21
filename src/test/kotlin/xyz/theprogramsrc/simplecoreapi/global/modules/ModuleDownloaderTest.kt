package xyz.theprogramsrc.simplecoreapi.global.modules

import org.apache.commons.io.FileUtils
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import xyz.theprogramsrc.simplecoreapi.standalone.StandaloneLoader
import java.io.File
import java.security.MessageDigest

internal class ModuleDownloaderTest {

    @Test
    fun `Test Files Module v0_2_1 Download`() {
        val file = ModuleManager.downloadModule("TheProgramSrc/SimpleCore-FilesModule", "v0.2.1-SNAPSHOT") ?: fail("Failed to download module")
        val digest = MessageDigest.getInstance("MD5")
        val md5 = digest.digest(file.readBytes()).joinToString("") { "%02x".format(it) }
        assertEquals("7efd5870362ece150ba2e63cfbd4847a", md5)
    }

    companion object {
        @BeforeAll
        @JvmStatic
        fun setUp() {
            StandaloneLoader()
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            arrayOf("SimpleCoreAPI/", "plugins/").map { File(it) }.filter{ it.exists() }.forEach { FileUtils.forceDelete(it) }
        }
    }
}