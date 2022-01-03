package xyz.theprogramsrc.simplecoreapi.global

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.net.URL
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.logging.Logger

/**
 * Representation of the GitHub Update Checker
 * @param logger The logger to use
 * @param repo The repository to check
 * @param currentVersion the current version (tag name) of the product
 */
class GitHubUpdateChecker(val logger: Logger, val repo: String, val currentVersion: String, val latestReleaseTag: String = "latest") {

    private var lastCheck = 0L
    private var lastCheckResult = false
    private var lastRequest = 0L
    private var latestData = JsonObject()
    private val current = if(currentVersion.startsWith("v")) currentVersion else "v$currentVersion"

    /**
     * Checks if there is an update available and prints
     * a message if there is one asking the end user to
     * update the product.
     */
    fun checkWithPrint() {
        val latestData = getLatestReleaseData()
        val latestVersion = latestData.get("tag_name").asString
        if(checkForUpdates()){
            logger.info("Please update (from $current to $latestVersion)! Download it now from here: https://github.com/$repo/releases/tag/$latestVersion")
        }
    }

    /**
     * Checks if there is an update available
     * @return true if there is an update available, false otherwise
     */
    fun checkForUpdates(): Boolean {
        val difference = System.currentTimeMillis() - lastCheck
        if(difference > 60000 || lastCheck == 0L){
            lastCheckResult = try {
                val parser = DateTimeFormatter.ISO_INSTANT
                val currentData = JsonParser.parseString(URL("https://api.github.com/repos/$repo/releases/tags/$current").readText()).asJsonObject
                val currentReleasedAt = Instant.from(parser.parse(currentData.get("published_at").asString))
                val latestData = getLatestReleaseData()
                val latestReleasedAt = Instant.from(parser.parse(latestData.get("published_at").asString))
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
     * Gets the information of the latest release
     * @return The information of the latest release
     */
    fun getLatestReleaseData(): JsonObject {
        val difference = System.currentTimeMillis() - lastRequest
        if(difference > 60000 || lastRequest == 0L){
            latestData = JsonParser.parseString(URL(if(latestReleaseTag != "latest") "https://api.github.com/repos/$repo/releases/tags/$latestReleaseTag" else "https://api.github.com/repos/$repo/releases/latest").readText()).asJsonObject
            lastRequest = System.currentTimeMillis()
        }
        return latestData
    }

}