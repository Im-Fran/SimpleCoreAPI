package xyz.theprogramsrc.simplecoreapi.global

import xyz.theprogramsrc.simplecoreapi.global.modules.ModuleManager
import xyz.theprogramsrc.simplecoreapi.global.utils.ILogger
import xyz.theprogramsrc.simplecoreapi.global.utils.SoftwareType
import xyz.theprogramsrc.simplecoreapi.global.utils.update.GitHubUpdateChecker
import xyz.theprogramsrc.simplecoreapi.standalone.StandaloneLoader
import java.io.File
import java.lang.RuntimeException

/**
 * Class used to initialize SimpleCoreAPI (DO NOT CALL IT FROM EXTERNAL PLUGINS, IT MAY CRASH)
 * @param logger The logger to use
 */
class SimpleCoreAPI(val logger: ILogger) {

    companion object {
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
         * @return The file relative to the data folder
         */
        fun dataFolder(path: String = ""): File = File(if (StandaloneLoader.isRunning) "./SimpleCoreAPI" else "plugins/SimpleCoreAPI", path).apply {
            if(!exists())
                mkdirs()
        }

        /**
         * Checks if the current [SoftwareType] is the one specified
         * @param softwareType The [SoftwareType] to check
         * @return true if the current [SoftwareType] is the one specified
         */
        fun isRunningSoftwareType(softwareType: SoftwareType) = softwareType.check()

        /**
         * The given module is added to the required modules list.
         * If the module is not found, it will be downloaded and automatically loaded.
         *
         * @param id The module id. Should be in the format author/repo
         */
        fun requireModule(id: String) {
            assert(id.split("/").size == 2) { "Invalid repositoryId format. It should be <author>/<repo>"}
            val isStandalone = isRunningSoftwareType(SoftwareType.STANDALONE) || isRunningSoftwareType(SoftwareType.UNKNOWN)
            val moduleFile = if(isStandalone) {
                File(dataFolder("modules"), "${id.split("/")[1]}.jar")
            } else {
                File(File("plugins/"), "${id.split("/")[1]}.jar")
            }

            if(moduleFile.exists()) {
                return
            }

            val downloaded = ModuleManager.downloadModule(id) ?: throw RuntimeException("Module $id could not be downloaded!")
            if(isStandalone) {
                return // Is automatically loaded later
            }

            // Load the module
            if(!ModuleManager.loadModule(downloaded)) {
                throw RuntimeException("Module $id could not be loaded!")
            }
        }
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
            GitHubUpdateChecker(logger, "TheProgramSrc/SimpleCoreAPI", getVersion()).checkWithPrint()
        }

        softwareType = SoftwareType.entries.firstOrNull { it.check() } ?: SoftwareType.UNKNOWN
        if(softwareType != SoftwareType.UNKNOWN && softwareType.display != null) {
            logger.info("Running API with software ${softwareType.display}")
        } else {
            logger.info("Running on unknown server software. Some features might not work as expected!")
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
    fun <OBJECT> measureLoad(message: String, block: () -> OBJECT): OBJECT {
        val now = System.currentTimeMillis()
        val obj = block()
        logger.info(message.replace("{time}", "${System.currentTimeMillis() - now}ms"))
        return obj
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