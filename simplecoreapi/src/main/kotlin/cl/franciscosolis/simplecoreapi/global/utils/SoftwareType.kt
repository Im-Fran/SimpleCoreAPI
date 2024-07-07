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

package cl.franciscosolis.simplecoreapi.global.utils

import cl.franciscosolis.simplecoreapi.standalone.StandaloneLoader
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
            Class.forName("com.velocitypowered.proxy.VelocityServer")
            true
        } catch(e: Exception) {
            false
        }
    }, "Velocity"),

    // Standalone
    STANDALONE(check = {
        StandaloneLoader.isRunning
    }, "Standalone"),

    UNKNOWN(check = {
        true
    }, "Unknown");
}