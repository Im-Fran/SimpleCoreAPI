package cl.franciscosolis.simplecoreapi.global.extensions

import java.util.logging.Logger

/**
 * Log a 'FINEST' message.
 *
 * If the logger is currently enabled for the 'FINEST' message
 * level then the given message is forwarded to all the
 * registered output Handler objects.
 *
 * @param msg The string message (or a key in the message catalog)
 */
fun Logger.debug(msg: String) = this.finest(msg)