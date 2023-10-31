package xyz.theprogramsrc.simplecoreapi.global.utils.update

import com.google.gson.JsonParser
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.net.URL

internal class SpigotUpdateCheckerTest {

    private val check1 = SpigotUpdateChecker("77825", URL("https://api.spigotmc.org/simple/0.1/index.php?action=getResource&id=77825").let {
        JsonParser.parseString(it.readText()).asJsonObject.get("current_version").asString.replace("v", "")
    })
    private val check2 = SpigotUpdateChecker("77825", "3.18.0")

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