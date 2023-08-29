package xyz.theprogramsrc.simplecoreapi.global.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import xyz.theprogramsrc.simplecoreapi.global.SimpleCoreAPI
import xyz.theprogramsrc.simplecoreapi.standalone.StandaloneLoader

internal class SoftwareTypeTest {

    @Test
    fun `Test SoftwareType detects Standalone mode`() {
        StandaloneLoader()

        assertEquals(SoftwareType.STANDALONE, SimpleCoreAPI.instance.softwareType)
    }
}