package xyz.theprogramsrc.simplecoreapi.global.utils

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
            val proxyServerInstance = Class.forName("net.md_5.bungee.api.ProxyServer").getMethod("getInstance").invoke(null) as Class<*>
            val name = proxyServerInstance.getMethod("getName").invoke(proxyServerInstance) as String
            name.equals("BungeeCord")
        } catch (e: Exception) {
            false
        }
    }, "BungeeCord"),

    WATERFALL(check = {
        try {
            val proxyServerInstance = Class.forName("net.md_5.bungee.api.ProxyServer").getMethod("getInstance").invoke(null) as Class<*>
            val name = proxyServerInstance.getMethod("getName").invoke(proxyServerInstance) as String
            name.equals("Waterfall")
        } catch (e: Exception) {
            false
        }
    }, "Waterfall"),

    UNKNOWN;
}