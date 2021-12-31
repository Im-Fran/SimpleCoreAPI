package xyz.theprogramsrc.simplecoreapi.global

import xyz.theprogramsrc.simplecoreapi.global.module.ModuleManager
import java.util.*
import java.util.logging.Logger

/**
 * Class used to initialize SimpleCoreAPI (DO NOT CALL IT FROM EXTERNAL PLUGINS, IT MAY CRASH)
 * @param logger The logger to use
 */
class SimpleCoreAPI(logger: Logger) {

    companion object {
        /**
         * Instance of SimpleCoreAPI. Use it to retrieve the module manager
         * @return The instance of SimpleCoreAPI
         */
        lateinit var instance: SimpleCoreAPI
    }

    /**
     * The Module Manager
     * @return The {@link ModuleManager}
     */
    val moduleManager: ModuleManager?

    /**
     * SimpleCoreAPI Properties
     * @return The {@link Properties} of SimpleCoreAPI
     */
    val props: Properties = Properties()

    init {
        instance = this
        val resource = SimpleCoreAPI::class.java.getResource("/simplecoreapi.properties")
        if (resource != null) {
            props.load(resource.openStream())
        }

        logger.info("SimpleCoreAPI v${getVersion()} - Git Commit: ${getShortHash()}")
        if (getVersion() != "unknown") {
            GitHubUpdateChecker(logger, "TheProgramSrc/SimpleCoreAPI", getVersion())
        }
        moduleManager = ModuleManager.init(logger)
    }

    /**
     * Gets the short version of the commit hash
     * @return The short commit hash
     */
    fun getShortHash(): String = props.getProperty("git-short", "unknown")

    /**
     * Gets the full version of the commit hash
     * @return The full commit hash
     */
    fun getFullHash(): String = props.getProperty("git-full", "unknown")

    /**
     * Gets the version of SimpleCoreAPI
     * @return The version of SimpleCoreAPI
     */
    fun getVersion(): String = props.getProperty("version", "unknown")
}