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

package cl.franciscosolis.simplecoreapi

import cl.franciscosolis.simplecoreapi.modules.filesmodule.extensions.file
import cl.franciscosolis.simplecoreapi.modules.filesmodule.extensions.folder
import cl.franciscosolis.simplecoreapi.utils.SoftwareType
import cl.franciscosolis.simplecoreapi.utils.update.GitHubUpdateChecker
import java.io.File
import java.util.logging.Logger

/**
 * Class used to initialize SimpleCoreAPI (DO NOT CALL IT FROM EXTERNAL PLUGINS, IT MAY CRASH)
 * @param logger The logger to use for the API (if null, it will use the default logger)
 */
class SimpleCoreAPI(private var logger: Logger? = null){

    companion object {
        /**
         * Instance of [Logger] used by [SimpleCoreAPI].
         * @return The instance of [Logger]
         */
        var logger: Logger = Logger.getAnonymousLogger()
            private set

        /**
         * Instance of SimpleCoreAPI. Use it to retrieve the module manager
         * @return The instance of SimpleCoreAPI
         */
        lateinit var instance: SimpleCoreAPI
            private set

        /**
         * Gets a file relative to the data folder.
         * If running in standalone mode the data folder will be ./SimpleCoreAPI, otherwise it will be the plugins/SimpleCoreAPI folder
         *
         * @param path The path of the file relative to the data folder
         * @param asFolder If true, it will return a folder, otherwise it will return a file
         * @return The file relative to the data folder
         */
        fun dataFolder(path: String = "", asFolder: Boolean = true): File = File(if (System.getProperties().containsKey("simplecoreapi.standalone")) "./SimpleCoreAPI" else "plugins/SimpleCoreAPI", path).let {
            if(asFolder) {
                it.folder()
            } else {
                it.file()
            }
        }

        /**
         * Checks if the current [SoftwareType] is the one specified
         * @param softwareType The [SoftwareType] to check
         * @return true if the current [SoftwareType] is the one specified
         */
        fun isRunningSoftwareType(softwareType: SoftwareType) = softwareType.check()
    }

    /**
     * The [SoftwareType] type running on the server
     * @return The [SoftwareType] the server is running
     */
    val softwareType: SoftwareType

    init {
        instance = this
        logger?.let { Companion.logger = it }

        Companion.logger.info("$getName v${getVersion} (Git Commit: ${getShortHash}). $getDescription")
        if (!getFullHash.contentEquals("unknown")) {
            GitHubUpdateChecker("TheProgramSrc/SimpleCoreAPI", getVersion)
                .checkWithPrint()
        }

        softwareType = SoftwareType.entries.firstOrNull { it.check() } ?: SoftwareType.UNKNOWN
        if(softwareType != SoftwareType.UNKNOWN && softwareType.display != null) {
            Companion.logger.info("Running API with software ${softwareType.display}")
        } else {
            Companion.logger.info("Running on unknown server software. Some features might not work as expected!")
        }
    }
}