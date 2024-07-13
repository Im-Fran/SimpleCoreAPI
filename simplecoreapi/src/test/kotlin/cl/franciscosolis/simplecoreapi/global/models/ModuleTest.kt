package cl.franciscosolis.simplecoreapi.global.models

import cl.franciscosolis.simplecoreapi.global.SimpleCoreAPI
import cl.franciscosolis.simplecoreapi.global.module.Module
import cl.franciscosolis.simplecoreapi.global.module.ModuleDescription
import cl.franciscosolis.simplecoreapi.global.module.isModuleLoaded
import cl.franciscosolis.simplecoreapi.global.module.requireModule
import cl.franciscosolis.simplecoreapi.standalone.StandaloneLoader
import org.apache.commons.io.FileUtils
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File

class ModuleTest {

    @Test
    fun `test require() method`() {
        assertFalse(isModuleLoaded<SumModule>()) // Validate that the module is not loaded

        val sumModule = requireModule<SumModule>() // Load the module
        assertTrue(isModuleLoaded<SumModule>()) // Validate that the module is loaded
        assertEquals(3, sumModule.sum(1, 2)) // Validate that the module is working

        assertEquals(3, requireModule<SumModule>().previous) // Validate that the module works even if it's loaded again
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

class SumModule: Module {

    override val description: ModuleDescription =
        ModuleDescription(
            name = "SumModule",
            version = "1.0",
            authors = listOf("TheProgramSrc")
        )

    var previous: Int = 0

    override fun onEnable() {
        SimpleCoreAPI.logger.info("Enabled DummyModule")
    }

    override fun onDisable() {
        SimpleCoreAPI.logger.info("Disabled DummyModule")
    }

    fun sum(a: Int, b: Int): Int {
        val sum = a + b
        previous = sum
        return sum
    }

}