package xyz.theprogramsrc.simplecoreapi.global

import com.google.gson.JsonParser
import xyz.theprogramsrc.simplecoreapi.global.dependencydownloader.DependencyDownloader
import xyz.theprogramsrc.simplecoreapi.global.models.Dependency
import xyz.theprogramsrc.simplecoreapi.global.models.Repository
import xyz.theprogramsrc.simplecoreapi.global.utils.ILogger
import xyz.theprogramsrc.simplecoreapi.global.utils.SoftwareType
import xyz.theprogramsrc.simplecoreapi.global.utils.update.GitHubUpdateChecker
import xyz.theprogramsrc.simplecoreapi.standalone.StandaloneLoader
import java.io.File
import java.net.URL

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

        // Now we'll download the module repository to automate the dependency downloader.
        val dependencyDownloader = measureLoad("Dependency Downloader loaded in {time}") {
            DependencyDownloader()
        }

        val modulesRepo = measureLoad("Downloaded module repository in {time}") {
            // Download the repo
            val destination = File(dataFolder(), "modules-repository.json")
            val content = URL("https://raw.githubusercontent.com/TheProgramSrc/GlobalDatabase/master/SimpleCoreAPI/modules-repository.json").readBytes()
            destination.writeBytes(content)

            JsonParser.parseString(String(content)).asJsonObject
        }

        measureLoad("Repositories and Dependencies loaded in {time}") {
            modulesRepo.getAsJsonArray("repositories").forEach { repo ->
                dependencyDownloader.addRepository(Repository(
                    url = repo.asString
                ))
            }

            modulesRepo.getAsJsonArray("dependencies").forEach { dependency ->
                val depend = Dependency (
                    group = dependency.asJsonObject.get("group").asString,
                    artifactId = dependency.asJsonObject.get("artifact").asString,
                    version = dependency.asJsonObject.get("version").asString
                )

                dependencyDownloader.addDependency(depend)
            }

            dependencyDownloader.loadDependencies()
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