package cl.franciscosolis.simplecoreapi.global

import cl.franciscosolis.simplecoreapi.*
import cl.franciscosolis.simplecoreapi.global.modules.filesmodule.extensions.file
import cl.franciscosolis.simplecoreapi.global.modules.filesmodule.extensions.folder
import cl.franciscosolis.simplecoreapi.global.utils.SoftwareType
import cl.franciscosolis.simplecoreapi.global.utils.update.GitHubUpdateChecker
import cl.franciscosolis.simplecoreapi.standalone.StandaloneLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.simple.SimpleLogger
import java.io.File

/**
 * Class used to initialize SimpleCoreAPI (DO NOT CALL IT FROM EXTERNAL PLUGINS, IT MAY CRASH)
 */
class SimpleCoreAPI {

    companion object {
        /**
         * Instance of SLF4 [Logger] used by [SimpleCoreAPI].
         * To change the log level, you must use the system properties command.
         * For example `-Dorg.slf4j.simpleLogger.defaultLogLevel=debug`
         * @return The instance of SLF4 [Logger]
         */
        val logger: Logger = let {
            System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, System.getenv()["LOG_LEVEL"]?.lowercase() ?: "info")
            LoggerFactory.getLogger("")
        }

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
        fun dataFolder(path: String = "", asFolder: Boolean = true): File = File(if (StandaloneLoader.isRunning) "./SimpleCoreAPI" else "plugins/SimpleCoreAPI", path).let {
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

        logger.info("$getName v${getVersion} (Git Commit: ${getShortHash}). $getDescription")
        if (!getFullHash.contentEquals("unknown")) {
            GitHubUpdateChecker("TheProgramSrc/SimpleCoreAPI", getVersion)
                .checkWithPrint()
        }

        softwareType = SoftwareType.entries.firstOrNull { it.check() } ?: SoftwareType.UNKNOWN
        if(softwareType != SoftwareType.UNKNOWN && softwareType.display != null) {
            logger.info("Running API with software ${softwareType.display}")
        } else {
            logger.info("Running on unknown server software. Some features might not work as expected!")
        }
    }
}