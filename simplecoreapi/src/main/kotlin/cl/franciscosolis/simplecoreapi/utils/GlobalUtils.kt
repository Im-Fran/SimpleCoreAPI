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

package cl.franciscosolis.simplecoreapi.utils

import cl.franciscosolis.simplecoreapi.SimpleCoreAPI

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
inline fun <T> measureLoad(message: String, block: () -> T): T {
    val now = System.currentTimeMillis()
    val response = block()
    SimpleCoreAPI.logger.info(message.replace("{time}", "${System.currentTimeMillis() - now}ms"))
    return response
}