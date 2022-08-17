package xyz.theprogramsrc.simplecoreapi.global.utils.update

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import xyz.theprogramsrc.simplecoreapi.global.utils.logger.*
import java.util.logging.Logger

internal class SongodaUpdateCheckerTest {

    private val check1 = SongodaUpdateChecker(JavaLogger(Logger.getLogger("SongodaUpdateCheckerTest - 1")), "255", "3.18.1")
    private val check2 = SongodaUpdateChecker(SLF4JLogger(org.slf4j.LoggerFactory.getLogger("SongodaUpdateCheckerTest - 2")), "255", "3.18.0")

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