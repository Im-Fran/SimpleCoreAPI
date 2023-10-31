package xyz.theprogramsrc.simplecoreapi.global.dependencydownloader

import org.apache.commons.io.FileUtils
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import xyz.theprogramsrc.simplecoreapi.global.models.module.Module
import xyz.theprogramsrc.simplecoreapi.standalone.StandaloneLoader
import java.io.File

internal class DependencyDownloaderTest {

    @Test
    fun `test modules repository is downloaded`() {
        assertTrue(File("SimpleCoreAPI/modules-repository.json").exists())
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
            Module.loadedModules.clear()
            arrayOf("SimpleCoreAPI/", "plugins/").map { File(it) }.filter{ it.exists() }.forEach { FileUtils.forceDelete(it) }
        }
    }
}