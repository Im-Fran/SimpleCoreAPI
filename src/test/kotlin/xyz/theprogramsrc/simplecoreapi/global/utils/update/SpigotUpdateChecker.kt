package xyz.theprogramsrc.simplecoreapi.global.utils.update

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import xyz.theprogramsrc.simplecoreapi.global.utils.logger.JavaLogger
import xyz.theprogramsrc.simplecoreapi.global.utils.logger.SLF4JLogger
import java.util.logging.Logger

internal class SpigotUpdateCheckerTest {

    private val check1 = SpigotUpdateChecker(JavaLogger(Logger.getLogger("SpigotUpdateCheckerTest - 1")), "77825", "3.18.1")
    private val check2 = SpigotUpdateChecker(SLF4JLogger(org.slf4j.LoggerFactory.getLogger("SpigotUpdateCheckerTest - 2")), "77825", "3.18.0")

    @Test
    fun getReleaseData() {
        val data = check1.getReleaseData()
        assertFalse(data.keySet().isEmpty())
    }

    @Test
    fun noUpdatesAvailableTest() {
        assertFalse(check1.checkForUpdates())
    }

    @Test
    fun updatesAvailableTest() {
        assertTrue(check2.checkForUpdates())
    }
}