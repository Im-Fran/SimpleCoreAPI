package xyz.theprogramsrc.simplecoreapi.global.models

/**
 * Representation of Dependency
 * @param group The group id of the dependency
 * @param artifactId The artifact id of the dependency
 * @param version The version of the dependency
 * @param md5Hash The md5 hash of the dependency, if null the downloader will not check the md5 hash (Which is not recommended)
 */
data class Dependency(val group: String, val artifactId: String, val version: String, val md5Hash: String? = null)