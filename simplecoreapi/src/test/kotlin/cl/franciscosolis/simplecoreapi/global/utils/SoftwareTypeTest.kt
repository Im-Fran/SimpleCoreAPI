package cl.franciscosolis.simplecoreapi.global.utils

import org.apache.commons.io.FileUtils
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import cl.franciscosolis.simplecoreapi.global.SimpleCoreAPI
import cl.franciscosolis.simplecoreapi.standalone.StandaloneLoader
import java.io.File

internal class SoftwareTypeTest {

    @Test
    fun `Test SoftwareType detects Standalone mode`() {
        assertEquals(SoftwareType.STANDALONE, SimpleCoreAPI.instance.softwareType)
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