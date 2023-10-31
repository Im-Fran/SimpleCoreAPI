package xyz.theprogramsrc.simplecoreapi.global

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.simple.SimpleLogger
import xyz.theprogramsrc.simplecoreapi.global.dependencydownloader.DependencyDownloader
import xyz.theprogramsrc.simplecoreapi.global.dependencydownloader.DependencyLoader
import xyz.theprogramsrc.simplecoreapi.global.dependencydownloader.interfaces.DependencyLoader
import xyz.theprogramsrc.simplecoreapi.global.utils.SoftwareType
import xyz.theprogramsrc.simplecoreapi.global.utils.extensions.file
import xyz.theprogramsrc.simplecoreapi.global.utils.extensions.folder
import xyz.theprogramsrc.simplecoreapi.global.utils.update.GitHubUpdateChecker
import xyz.theprogramsrc.simplecoreapi.standalone.StandaloneLoader
import java.io.File

/**
 * Class used to initialize SimpleCoreAPI (DO NOT CALL IT FROM EXTERNAL PLUGINS, IT MAY CRASH)
 * @param dependencyClassLoader The [DependencyLoader] to use to load the dependencies
 */
class SimpleCoreAPI(
    private val dependencyClassLoader: xyz.theprogramsrc.simplecoreapi.global.dependencydownloader.interfaces.DependencyLoader
){

    companion object {
        /**
         * Instance of SLF4 [Logger] used by [SimpleCoreAPI].
         * In order to change the log level you must use the system properties command, for example `-Dorg.slf4j.simpleLogger.defaultLogLevel=debug`
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

        logger.info("SimpleCoreAPI v${getVersion()} - Git Commit: ${getShortHash()}")
        if (getFullHash() != "unknown") {
            GitHubUpdateChecker("TheProgramSrc/SimpleCoreAPI", getVersion()).checkWithPrint()
        }

        softwareType = SoftwareType.entries.firstOrNull { it.check() } ?: SoftwareType.UNKNOWN
        if(softwareType != SoftwareType.UNKNOWN && softwareType.display != null) {
            logger.info("Running API with software ${softwareType.display}")
        } else {
            logger.info("Running on unknown server software. Some features might not work as expected!")
        }

        // Now we'll download the dependencies
        measureLoad("Downloaded dependencies in {time}") {
            DependencyDownloader()
        }

        // Now we load the downloaded dependencies
        measureLoad("Loaded dependencies in {time}") {
            DependencyLoader(dependencyClassLoader = dependencyClassLoader)
        }
    }

    /**
     * Measures the amount of time in milliseconds it takes to execute the given block. Example:
     * ```kt
     * measureLoad("Waited for {time}") {
     *    // wait for 100 ms
     *    Thread.sleep(100)
     * }
     * ```
     *
     * Sample console output:
     * ```log
     * Waited for 100ms
     * ```
     * @param message The message to print. You can use '{time}' to replace with the amount of time in ms
     * @param block The block to execute
     */
    fun <T> measureLoad(message: String, block: () -> T): T {
        val now = System.currentTimeMillis()
        val response = block()
        logger.info(message.replace("{time}", "${System.currentTimeMillis() - now}ms"))
        return response
    }

    /**
     * Gets the short version of the commit hash
     * @return The short commit hash
     */
    fun getShortHash(): String = "@git_short@"

    /**
     * Gets the full version of the commit hash
     * @return The full commit hash
     */
    fun getFullHash(): String = "@git_full@"

    /**
     * Gets the version of SimpleCoreAPI
     * @return The version of SimpleCoreAPI
     */
    fun getVersion(): String = "@version@"
}