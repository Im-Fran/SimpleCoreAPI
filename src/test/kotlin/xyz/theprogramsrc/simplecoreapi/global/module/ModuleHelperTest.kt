package xyz.theprogramsrc.simplecoreapi.global.module

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import xyz.theprogramsrc.simplecoreapi.global.SimpleCoreAPI

internal class ModuleHelperTest {

    @Test
    fun downloadModule() {
        assertTrue(ModuleHelper.downloadModule("TheProgramSrc/SimpleCore-TasksModule", "TasksModule")) // Test the download
        assertTrue(SimpleCoreAPI.dataFolder("modules/TasksModule.jar").exists())
        SimpleCoreAPI.dataFolder().deleteRecursively() // Recursively delete the plugins folder
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
            "filesmodule",
            "configmodule",
            "loggingmodule",
            "translationsmodule",
            "tasksmodule",
            "uismodule",
        )



        assertEquals(expectedOrder, ModuleHelper.sortModuleDependencies(dependencies))
    }
}