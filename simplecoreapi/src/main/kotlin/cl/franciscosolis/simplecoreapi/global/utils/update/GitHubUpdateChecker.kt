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

package cl.franciscosolis.simplecoreapi.global.utils.update

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import cl.franciscosolis.simplecoreapi.global.SimpleCoreAPI
import java.net.URI
import java.time.Instant
import java.time.format.DateTimeFormatter

/**
 * With this class you can check if there is an update available using the GitHub releases API.
 * Sample usage:
 * ```kt
 * GitHubUpdateChecker("TheProgramSrc/SimpleCoreAPI", "v0.4.1-SNAPSHOT")
 *    .checkWithPrint() // This will print the message if there is an update available
 *
 * // You can also check if there is an update available without printing a message
 * val githubUpdateChecker = GitHubUpdateChecker(logger, "TheProgramSrc/SimpleCoreAPI", "v0.4.1-SNAPSHOT")
 * if(githubUpdateChecker.checkForUpdates()){
 *    // There is an update available
 *    // Do something here
 *    // For example:
 *    logger.info("Please update! Download it now from here: https://example.com/download")
 *    // Or
 *    logger.info("Please update! Download it now from here: ${githubUpdateChecker.getReleaseData().get("url").asString}")
 * }
 * ```
 *
 * @param repo The repository to check. The format should be 'Holder/Repository', for example 'TheProgramSrc/SimpleCoreAPI'
 * @param currentVersion the current version (tag name) of the product. (Example: "v0.1.0-SNAPSHOT")
 * @param latestReleaseTag The tag name of the latest release. (Defaults to "latest")
 */
class GitHubUpdateChecker(
    val repo: String,
    val currentVersion: String,
    val latestReleaseTag: String = "latest"
): UpdateChecker {

    private val logger = SimpleCoreAPI.logger
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
            val json = JsonParser.parseString(URI.create(if(id != "latest") "https://api.github.com/repos/$repo/releases/tags/$id" else "https://api.github.com/repos/$repo/releases/latest").toURL().readText()).asJsonObject
            cached = Pair(JsonObject().apply {
                addProperty("published_at", json.get("published_at").asString)
                addProperty("version", json.get("tag_name").asString)
                addProperty("url", json.get("html_url").asString)
                addProperty("author_url", json.get("author").asJsonObject.get("html_url").asString)
            }, System.currentTimeMillis())
            requestedData[id] = cached
        }
        return cached.first
    }

}