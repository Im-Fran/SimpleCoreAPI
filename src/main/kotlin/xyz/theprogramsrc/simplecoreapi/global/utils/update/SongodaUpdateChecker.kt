package xyz.theprogramsrc.simplecoreapi.global.utils.update

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import xyz.theprogramsrc.simplecoreapi.global.utils.ILogger
import java.net.URL
import java.time.Instant
import java.time.format.DateTimeFormatter

class SongodaUpdateChecker(val logger: ILogger, val productId: String, val currentVersion: String): UpdateChecker {

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
            logger.info("Please update (from $current to $latestVersion)! Download it now from here: https://marketplace.songoda.org/marketplace/product/$productId")
        }
    }

    /**
     * Checks if there is an update available
     * @return true if there is an update available, false otherwise
     */
    override fun checkForUpdates(): Boolean {
        val difference = System.currentTimeMillis() - lastCheck
        if(difference > 60000 || lastCheck == 0L) {
            lastCheckResult = try {
                val parser = DateTimeFormatter.ISO_INSTANT
                val currentReleasedAt = Instant.from(parser.parse(getReleaseData(currentVersion).get("published_at").asString))
                val latestReleasedAt = Instant.from(parser.parse(getReleaseData().get("published_at").asString))
                currentReleasedAt.isBefore(latestReleasedAt)
            }catch (e: Exception) {
                e.printStackTrace()
                false
            }
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
            val url = if(id == "latest"){
                "https://marketplace.songoda.com/api/v2/products/id/$productId/versions?sort=-created_at&per_page=1"
            } else {
                "https://marketplace.songoda.com/api/v2/products/id/$productId/versions?sort=-created_at&per_page=1&filter[version]=$id"
            }

            val data = JsonParser.parseString(URL(url).readText()).asJsonObject.getAsJsonArray("data")
            if(data.isEmpty){
                throw RuntimeException("We couldn't find any data for the product with id '$productId' and version '$id'. Please try again later.")
            }
            val json = data.get(0).asJsonObject

            cached = Pair(JsonObject().apply {
                addProperty("published_at", DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochMilli(json.get("created_at").asLong * 1000L)))
                addProperty("version", json.get("version").asString)
                addProperty("url", json.get("url").asString)
                addProperty("author_url", "https://marketplace.songoda.com/profiles/${json.get("uploaded_by").asJsonObject.get("name").asString}")
                }, System.currentTimeMillis())
            requestedData[id] = cached
        }
        return cached.first
    }

}