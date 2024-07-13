/*
 * SimpleCoreAPI - Kotlin Project Library
 * Copyright (C) 2024 Francisco Solís
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package cl.franciscosolis.simplecoreapi.global.utils.update

import com.google.gson.JsonParser
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.net.URI

internal class SpigotUpdateCheckerTest {

    private val check1 = SpigotUpdateChecker("77825", URI.create("https://api.spigotmc.org/simple/0.1/index.php?action=getResource&id=77825").toURL().let {
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