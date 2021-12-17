package xyz.theprogramsrc.simplecoreapi.global.module

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

internal class ModuleHelperTest {

    @Test
    fun downloadModule() {
        assertTrue(ModuleHelper.downloadModule("loggingmodule")) // Test the download
        File("plugins/").deleteRecursively() // Recursively delete the plugins folder
    }
}