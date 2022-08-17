package xyz.theprogramsrc.simplecoreapi.global.utils.update

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import xyz.theprogramsrc.simplecoreapi.global.utils.ILogger
import java.net.URL
import java.time.Instant
import java.time.format.DateTimeFormatter

/**
 * Representation of the GitHub Update Checker
 * @param logger The logger to use, it must be an instance of [ILogger]
 * @param repo The repository to check. The format should be <Holder>/<Repository>, for example TheProgramSrc/SimpleCoreAPI
 * @param currentVersion the current version (tag name) of the product
 * @param latestReleaseTag The tag name of the latest release. (Defaults to "latest")
 */
class GitHubUpdateChecker(val logger: ILogger, val repo: String, val currentVersion: String, val latestReleaseTag: String = "latest"): UpdateChecker {

    private var lastCheck = 0L
    private var lastCheckResult = false
    private val requestedData = mutableMapOf<String, Pair<JsonObject, Long>>()
    private val current = if(currentVersion.startsWith("v")) currentVersion else "v$currentVersion"

    /**
     * Checks if there is an update available and prints
     * a message if there is one asking the end user to
     * update the product.
     */
    override fun checkWithPrint() {
        val latestData = getReleaseData()
        val latestVersion = latestData.get("version").asString
        if(checkForUpdates()){
            logger.info("Please update (from $current to $latestVersion)! Download it now from here: https://github.com/$repo/releases/tag/$latestVersion")
        }
    }

    /**
     * Checks if there is an update available
     * @return true if there is an update available, false otherwise
     */
    override fun checkForUpdates(): Boolean {
        val difference = System.currentTimeMillis() - lastCheck
        if(difference > 60000 || lastCheck == 0L){
            lastCheckResult = try {
                val parser = DateTimeFormatter.ISO_INSTANT
                val currentReleasedAt = Instant.from(parser.parse(getReleaseData(current).get("published_at").asString))
                val latestReleasedAt = Instant.from(parser.parse(getReleaseData(latestReleaseTag).get("published_at").asString))
                Instant.from(currentReleasedAt).isBefore(latestReleasedAt)
            }catch (e: Exception){
                e.printStackTrace()
                false
            }

            lastCheck = System.currentTimeMillis()
        }
        return lastCheckResult
    }

    /**
     * Gets the information of a single release
     * Object Sample:
     * { "published_at": "2022-07-15T21:51:46.397962Z", "version": "v0.4.1-SNAPSHOT", "url": "https://github.com/TheProgramSrc/SimpleCoreAPI/releases/tag/v0.4.1-SNAPSHOT", "author_url": "https://github.com/Im-Fran" }
     * - published_at: Is the date when the version was made public. This date must be able to be parsed by Instant#from
     * - version: The version of the latest asset
     * - url: The url to the version page (null if not available)
     * - author_url: The url to the author profile (null if not available)
     *
     * @param id the name of the release. (If none specified the latest data is fetched. Defaults to "latest")
     * @return The information of the given release name
     * @since 0.4.1-SNAPSHOT
     */
    override fun getReleaseData(id: String): JsonObject {
        var cached = requestedData.getOrDefault(id, Pair(JsonObject(), 0L))
        val difference = System.currentTimeMillis() - cached.second
        if(difference > 60000 || cached.second == 0L){
            val json = JsonParser.parseString(URL(if(id != "latest") "https://api.github.com/repos/$repo/releases/tags/$id" else "https://api.github.com/repos/$repo/releases/latest").readText()).asJsonObject
            cached = Pair(JsonObject().apply {
                addProperty("published_at", json.get("published_at").asString)
                addProperty("version", json.get("tag_name").asString)
                addProperty("url", json.get("html_url").asString)
                addProperty("author_url", json.get("author").asJsonObject.get("html_url").asString)
            }, System.currentTimeMillis())
            requestedData[id] = cached
        }
        println(cached.first.toString() + " - $id")
        return cached.first
    }

}