package xyz.theprogramsrc.simplecoreapi.global

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import xyz.theprogramsrc.simplecoreapi.global.utils.logger.JavaLogger
import xyz.theprogramsrc.simplecoreapi.global.utils.logger.SLF4JLogger
import java.util.logging.Logger

internal class GitHubUpdateCheckerTest {

    private val check1 = GitHubUpdateChecker(JavaLogger(Logger.getLogger("GitHubUpdateCheckerTest - 1")), "TheProgramSrc/SimpleCoreAPI", "0.3.0-SNAPSHOT", "v0.3.0-SNAPSHOT")
    private val check2 = GitHubUpdateChecker(SLF4JLogger(org.slf4j.LoggerFactory.getLogger("GitHubUpdateCheckerTest - 2")), "TheProgramSrc/SimpleCoreAPI", "0.3.0-SNAPSHOT")

    @Test
    fun noUpdatesAvailableTest() {
        assertEquals(false, check1.checkForUpdates())
    }

    @Test
    fun updatesAvailableTest() {
        assertEquals(true, check2.checkForUpdates())
    }
}