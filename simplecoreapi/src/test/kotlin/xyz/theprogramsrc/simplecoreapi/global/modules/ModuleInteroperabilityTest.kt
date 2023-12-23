package xyz.theprogramsrc.simplecoreapi.global.modules

import org.apache.commons.io.FileUtils
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import xyz.theprogramsrc.simplecoreapi.global.SimpleCoreAPI
import xyz.theprogramsrc.simplecoreapi.global.models.SumModule
import xyz.theprogramsrc.simplecoreapi.global.module.Module
import xyz.theprogramsrc.simplecoreapi.global.module.ModuleDescription
import xyz.theprogramsrc.simplecoreapi.global.module.isModuleLoaded
import xyz.theprogramsrc.simplecoreapi.global.module.requireModule
import xyz.theprogramsrc.simplecoreapi.standalone.StandaloneLoader
import java.io.File


// This will test if modules are able to call methods from other modules.
internal class ModuleInteroperabilityTest {

    @Test
    fun `test module interoperability`() {
        assertFalse(isModuleLoaded<SumModule>()) // Validate that the module is not loaded
        val mathModule = requireModule<MathModule>()
        assertTrue(isModuleLoaded<SumModule>()) // Validate that the module is loaded
        assertEquals(0, mathModule.prev()) // Validate that the module is working
        assertEquals(3, mathModule.sum(1, 2)) // Validate that the module is working
        assertEquals(3, mathModule.prev()) // Validate that the module is working

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

class MathModule: Module {
    override val description: ModuleDescription =
        ModuleDescription(
            name = "MathModule",
            version = "1.0",
            authors = listOf("TheProgramSrc")
        )

    val sum = requireModule<SumModule>()

    override fun onEnable() {

    }

    override fun onDisable() {

    }

    fun sum(a: Int, b: Int) =
        sum.sum(a, b)

    fun prev() =
        sum.previous

}