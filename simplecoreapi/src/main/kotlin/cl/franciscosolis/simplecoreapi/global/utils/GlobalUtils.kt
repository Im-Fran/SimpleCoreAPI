package cl.franciscosolis.simplecoreapi.global.utils

import cl.franciscosolis.simplecoreapi.global.SimpleCoreAPI

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