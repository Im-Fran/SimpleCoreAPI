package xyz.theprogramsrc.simplecoreapi.global

import xyz.theprogramsrc.simplecoreapi.global.module.ModuleManager
import xyz.theprogramsrc.simplecoreapi.global.utils.SoftwareType
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
            private set
    }

    /**
     * The Module Manager
     * @return The [ModuleManager]
     */
    val moduleManager: ModuleManager?

    /**
     * SimpleCoreAPI Properties
     * @return The [Properties] of SimpleCoreAPI
     */
    val props: Properties = Properties()

    /**
     * The [SoftwareType] type running on the server
     * @return The [SoftwareType] the server is running
     */
    val softwareType: SoftwareType

    init {
        instance = this
        val resource = SimpleCoreAPI::class.java.getResource("/simplecoreapi.properties")
        if (resource != null) {
            props.load(resource.openStream())
        }

        logger.info("SimpleCoreAPI v${getVersion()} - Git Commit: ${getShortHash()}")
        if (getFullHash() != "unknown") {
            GitHubUpdateChecker(logger, "TheProgramSrc/SimpleCoreAPI", getVersion()).checkWithPrint()
        }
        softwareType = SoftwareType.values().firstOrNull { it.check() } ?: SoftwareType.UNKNOWN
        if(softwareType != SoftwareType.UNKNOWN && softwareType.display != null) {
            logger.info("Running API with software ${softwareType.display}")
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

    /**
     * Checks if the current [SoftwareType] is the one specified
     * @param softwareType The [SoftwareType] to check
     * @return true if the current [SoftwareType] is the one specified
     */
    fun isRunningSoftwareType(softwareType: SoftwareType) = softwareType.check()
}