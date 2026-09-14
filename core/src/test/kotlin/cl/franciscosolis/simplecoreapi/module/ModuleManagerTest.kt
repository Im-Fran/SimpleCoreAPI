package cl.franciscosolis.simplecoreapi.module

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ModuleManagerTest {

    @BeforeEach
    fun reset() {
        ModuleManager.disableAll()
        shutdownOrder.clear()
        instantiations.clear()
    }

    @Test
    fun `always returns the same instance`() {
        assertSame(requireModule<SimpleModule>(), requireModule<SimpleModule>())
        assertEquals(1, instantiations.count { it == "simple" })
    }

    @Test
    fun `nothing is instantiated until it is requested`() {
        assertFalse(ModuleManager.isLoaded(SimpleModule::class.java))
        assertTrue(instantiations.isEmpty())

        requireModule<SimpleModule>()

        assertTrue(ModuleManager.isLoaded(SimpleModule::class.java))
    }

    @Test
    fun `onEnable runs exactly once`() {
        val module = requireModule<SimpleModule>()
        requireModule<SimpleModule>()
        assertEquals(1, module.enabled)
    }

    @Test
    fun `a module can request another one from its constructor`() {
        val dependent = requireModule<DependentModule>()

        assertSame(requireModule<SimpleModule>(), dependent.dependency)
        assertEquals(listOf(SimpleModule::class.java, DependentModule::class.java), ModuleManager.loaded)
    }

    @Test
    fun `a circular dependency reports the whole chain`() {
        val error = assertThrows<IllegalStateException> { requireModule<CircularA>() }

        assertContains(error.message!!, "CircularA -> CircularB -> CircularA")
    }

    @Test
    fun `a module without a no-argument constructor fails with a clear message`() {
        val error = assertThrows<IllegalStateException> {
            ModuleManager.require(NeedsArgumentsModule::class.java)
        }

        assertContains(error.message!!, "public no-argument constructor")
    }

    @Test
    fun `disableAll shuts down in reverse load order and clears the registry`() {
        requireModule<DependentModule>() // loads SimpleModule first, then DependentModule

        ModuleManager.disableAll()

        assertEquals(listOf("dependent", "simple"), shutdownOrder)
        assertTrue(ModuleManager.loaded.isEmpty())
        assertFalse(ModuleManager.isLoaded(SimpleModule::class.java))
    }

    @Test
    fun `a failure while disabling does not stop the remaining modules`() {
        requireModule<FailingModule>()
        requireModule<SimpleModule>()

        ModuleManager.disableAll()

        assertEquals(listOf("simple"), shutdownOrder)
        assertTrue(ModuleManager.loaded.isEmpty())
    }

    companion object {
        val shutdownOrder = mutableListOf<String>()
        val instantiations = mutableListOf<String>()
    }

    class SimpleModule : Module {
        var enabled = 0

        init {
            instantiations += "simple"
        }

        override fun onEnable() {
            enabled++
        }

        override fun onDisable() {
            shutdownOrder += "simple"
        }
    }

    class DependentModule : Module {
        val dependency: SimpleModule = requireModule()

        override fun onDisable() {
            shutdownOrder += "dependent"
        }
    }

    class CircularA : Module {
        @Suppress("unused")
        val other: CircularB = requireModule()
    }

    class CircularB : Module {
        @Suppress("unused")
        val other: CircularA = requireModule()
    }

    class NeedsArgumentsModule(@Suppress("unused") val name: String) : Module

    class FailingModule : Module {
        override fun onDisable(): Unit = throw IllegalStateException("boom")
    }
}
