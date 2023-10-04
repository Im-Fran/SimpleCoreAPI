package xyz.theprogramsrc.simplecoreapi.global.models

import org.apache.commons.io.FileUtils
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import xyz.theprogramsrc.simplecoreapi.standalone.StandaloneLoader
import java.io.File

class ModuleTest {

    @Test
    fun `test require() method`() {
        val mathModule = requireModule<MathModule>()

        assertEquals(1, MathModule.loaded)
        assertEquals(3, mathModule.sum(1, 2))
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

class MathModule: Module() {

    companion object {
        var loaded = 0
    }

    override fun onEnable() {
        logger.info("Enabled DummyModule")
        loaded++
    }

    override fun onDisable() {
        logger.info("Disabled DummyModule")
    }

    fun sum(a: Int, b: Int) =
        a + b

}

class AlgebraModule: Module() {

    override fun onEnable() {
        logger.info("Enabled AlgebraModule")
    }

    override fun onDisable() {
        logger.info("Disabled AlgebraModule")
    }

    fun solveEquation(a: Int, b: Int, c: Int): Int {
        val delta = b * b - 4 * a * c
        return if (delta < 0) {
            -1
        } else if (delta == 0) {
            0
        } else {
            1
        }
    }

}