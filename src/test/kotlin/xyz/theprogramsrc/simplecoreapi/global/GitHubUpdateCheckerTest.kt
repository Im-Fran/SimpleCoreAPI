package xyz.theprogramsrc.simplecoreapi.global

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.logging.Logger

internal class GitHubUpdateCheckerTest {

    private val check1 = GitHubUpdateChecker(Logger.getLogger("GitHubUpdateCheckerTest - 1"), "TheProgramSrc/SimpleCoreAPI", "0.1.9-SNAPSHOT", "v0.1.9-SNAPSHOT")
    private val check2 = GitHubUpdateChecker(Logger.getLogger("GitHubUpdateCheckerTest - 2"), "TheProgramSrc/SimpleCoreAPI", "0.1.6-SNAPSHOT")

    @Test
    fun noUpdatesAvailableTest() {
        assertEquals(false, check1.checkForUpdates())
    }

    @Test
    fun updatesAvailableTest() {
        assertEquals(true, check2.checkForUpdates())
    }
}