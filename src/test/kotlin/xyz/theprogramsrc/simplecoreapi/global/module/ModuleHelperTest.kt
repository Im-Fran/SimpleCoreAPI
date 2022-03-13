package xyz.theprogramsrc.simplecoreapi.global.module

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

internal class ModuleHelperTest {

    @Test
    fun downloadModule() {
        assertTrue(ModuleHelper.downloadModule("loggingmodule")) // Test the download
        File("plugins/").deleteRecursively() // Recursively delete the plugins folder
    }

    @Test
    fun sortModules(){
        val dependencies = mutableMapOf<String, Collection<String>>(
            "configmodule" to listOf("filesmodule"),
            "loggingmodule" to listOf("configmodule"),
            "filesmodule" to emptyList(),
            "translationsmodule" to listOf("loggingmodule", "filesmodule"),
            "uismodule" to listOf("tasksmodule", "translationsmodule"),
            "tasksmodule" to emptyList(),
        )

        val expectedOrder = listOf(
            // First modules without dependencies (in order of appearance)
            "filesmodule",
            "tasksmodule",
            // Then modules with dependencies (in order so other modules can depend on them)
            "configmodule",
            "loggingmodule",
            "translationsmodule",
            "uismodule",
        )



        assertEquals(expectedOrder, ModuleHelper.sortModuleDependencies(dependencies))
    }
}