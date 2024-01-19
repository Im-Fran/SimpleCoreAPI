package cl.franciscosolis.simplecoreapi.global.utils.update

import com.google.gson.JsonObject

interface UpdateChecker {

    /**
     * Checks if there is an update available and prints
     * a message if there is one asking the end user to
     * update the product.
     */
    fun checkWithPrint()

    /**
     * Checks if there is an update available
     * @return true if there is an update available, false otherwise
     */
    fun checkForUpdates(): Boolean

    /**
     * Gets the information of a single release
     * Object Sample:
     * { "published_at": "2022-07-15T21:51:46.397962Z", "version": "v0.4.1-SNAPSHOT", "url": "https://github.com/TheProgramSrc/SimpleCoreAPI/releases/tag/v0.4.1-SNAPSHOT", "author_url": "https://github.com/Im-Fran" }
     * - published_at: Is the date when the version was made public. This date must be able to be parsed by Instant#from
     * - version: The version of the latest asset
     * - url: The url to the version page (null if not available)
     * - author_url: The url to the author profile (null if not available)
     *
     * @param id the name of the release. (If none specified, the latest data is fetched. Defaults to "latest")
     * @return The information of the given release name
     * @since 0.4.1-SNAPSHOT
     */
    fun getReleaseData(id: String = "latest"): JsonObject
}