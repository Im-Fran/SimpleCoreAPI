package cl.franciscosolis.simplecoreapi.global.utils.update

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class GitHubUpdateCheckerTest {

    private val check1 = GitHubUpdateChecker("TheProgramSrc/SimpleCoreAPI", "0.4.0-SNAPSHOT", "v0.4.0-SNAPSHOT")
    private val check2 = GitHubUpdateChecker("TheProgramSrc/SimpleCoreAPI", "0.3.6-SNAPSHOT")

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
