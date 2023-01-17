package xyz.theprogramsrc.simplecoreapi.global.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import xyz.theprogramsrc.simplecoreapi.global.SimpleCoreAPI
import xyz.theprogramsrc.simplecoreapi.standalone.StandaloneLoader

internal class SoftwareTypeTest {

    @Test
    fun TestStandaloneSoftwareType() {
        StandaloneLoader()

        assertEquals(SoftwareType.STANDALONE, SimpleCoreAPI.instance.softwareType)
    }

}