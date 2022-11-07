package xyz.theprogramsrc.simplecoreapi.global.utils

import java.util.Objects

/**
 * Representation of a ServerSoftware
 * @param check The function to check if the software is running the server or not.
 */
enum class SoftwareType(val check: () -> Boolean = { false }, val display: String? = null) {
    // Servers
    BUKKIT(check = {
        try {
            val bukkit = Class.forName("org.bukkit.Bukkit")
            val version = bukkit.getMethod("getVersion").invoke(bukkit) as String
            version.contains("-Bukkit-")
        } catch (e: Exception) {
            false
        }
    }, "Bukkit"),

    SPIGOT(check = {
        try {
            val bukkit = Class.forName("org.bukkit.Bukkit")
            val version = bukkit.getMethod("getVersion").invoke(bukkit) as String
            version.contains("-Spigot-")
        } catch (e: Exception) {
            false
        }
    }, "Spigot"),

    PAPER(check = {
        try {
            val bukkit = Class.forName("org.bukkit.Bukkit")
            val version = bukkit.getMethod("getVersion").invoke(bukkit) as String
            version.contains("-Paper-")
        } catch (e: Exception) {
            false
        }
    }, "Paper"),

    PURPUR(check = {
        try {
            val bukkit = Class.forName("org.bukkit.Bukkit")
            val version = bukkit.getMethod("getVersion").invoke(bukkit) as String
            version.contains("-Purpur-")
        } catch (e: Exception) {
            false
        }
    }, "Purpur"),

    // Proxies
    BUNGEE(check = {
        try {
            val proxyServer = Class.forName("net.md_5.bungee.api.ProxyServer")
            val proxyServerInstanceValue = proxyServer.getDeclaredField("instance").apply {
                isAccessible = true
            }.get(null)
            val proxyServerName = proxyServer.getMethod("getName").invoke(proxyServerInstanceValue) as String
            Objects.equals(proxyServerName, "BungeeCord")
        } catch (e: Exception) {
            false
        }
    }, "BungeeCord"),

    WATERFALL(check = {
        try {
            val proxyServer = Class.forName("net.md_5.bungee.api.ProxyServer")
            val proxyServerInstanceValue = proxyServer.getDeclaredField("instance").apply {
                isAccessible = true
            }.get(null)
            val proxyServerName = proxyServer.getMethod("getName").invoke(proxyServerInstanceValue) as String
            Objects.equals(proxyServerName, "Waterfall")
        } catch (e: Exception) {
            false
        }
    }, "Waterfall"),

    VELOCITY(check = {
        try {
            val proxyServer = Class.forName("com.velocitypowered.api.util.ProxyVersion")
            true
        } catch(e: Exception) {
            false
        }
    }, "Velocity"),

    UNKNOWN;
}