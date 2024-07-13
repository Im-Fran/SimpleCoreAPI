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

package cl.franciscosolis.simplecoreapi.global.utils

import org.apache.commons.io.FileUtils
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import cl.franciscosolis.simplecoreapi.global.SimpleCoreAPI
import cl.franciscosolis.simplecoreapi.standalone.StandaloneLoader
import java.io.File

internal class SoftwareTypeTest {

    @Test
    fun `Test SoftwareType detects Standalone mode`() {
        assertEquals(SoftwareType.STANDALONE, SimpleCoreAPI.instance.softwareType)
    }

    companion object {
        @BeforeAll
        @JvmStatic
        fun setUp() {
            StandaloneLoader()
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            arrayOf("SimpleCoreAPI/", "plugins/").map { File(it) }.filter{ it.exists() }.forEach { FileUtils.forceDelete(it) }
        }
    }
}