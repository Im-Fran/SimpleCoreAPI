package xyz.theprogramsrc.simplecoreapi.global.utils.update

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import xyz.theprogramsrc.simplecoreapi.global.utils.logger.*
import java.util.logging.Logger

internal class GitHubUpdateCheckerTest {

    private val check1 = GitHubUpdateChecker(JavaLogger(Logger.getLogger("GitHubUpdateCheckerTest - 1")), "TheProgramSrc/SimpleCoreAPI", "0.4.0-SNAPSHOT", "v0.4.0-SNAPSHOT")
    private val check2 = GitHubUpdateChecker(SLF4JLogger(org.slf4j.LoggerFactory.getLogger("GitHubUpdateCheckerTest - 2")), "TheProgramSrc/SimpleCoreAPI", "0.3.6-SNAPSHOT")

    @Test
    fun noUpdatesAvailableTest() {
        assertFalse(check1.checkForUpdates())
    }

    @Test
    fun updatesAvailableTest() {
        assertTrue(check2.checkForUpdates())
    }
}
