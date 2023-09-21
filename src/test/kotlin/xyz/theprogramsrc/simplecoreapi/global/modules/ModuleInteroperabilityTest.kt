package xyz.theprogramsrc.simplecoreapi.global.modules

import org.apache.commons.io.FileUtils
import org.junit.jupiter.api.AfterAll
import xyz.theprogramsrc.simplecoreapi.global.SimpleCoreAPI
import xyz.theprogramsrc.simplecoreapi.standalone.EntryPoint
import xyz.theprogramsrc.simplecoreapi.standalone.EntrypointLoader
import xyz.theprogramsrc.simplecoreapi.standalone.StandaloneLoader
import java.io.File


// This will test if modules are able to call methods from other modules.
internal class ModuleInteroperabilityTest {

    //@Test
    fun `Test if module can call methods from other modules`() {
        // First we register the entrypoint
        EntrypointLoader.registerEntrypoint(MockApp::class.java)

        // Start the standalone loader.
        StandaloneLoader()
    }

    companion object {

        @AfterAll
        @JvmStatic
        fun tearDown() {
            arrayOf("SimpleCoreAPI/", "plugins/").map { File(it) }.filter{ it.exists() }.forEach { FileUtils.forceDelete(it) }
        }
    }
}

class MockApp: EntryPoint {

    override fun onLoad() {
        println("onLoad")
        SimpleCoreAPI.requireModule("TheProgramSrc/SimpleCore-TranslationsModule") // Require the TranslationsModule
    }

    override fun onEnable() {
        println("onEnable")
    }

    override fun onDisable() {
        println("onDisable")
    }
}