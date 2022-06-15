package xyz.theprogramsrc.simplecoreapi.global.utils.logger

import xyz.theprogramsrc.simplecoreapi.global.utils.ILogger
import java.util.logging.Level

import java.util.logging.Logger

class JavaLogger(val logger: Logger): ILogger {

    override fun info(message: String) {
        logger.info(message)
    }

    override fun warn(message: String) {
        logger.warning(message)
    }

    override fun error(message: String) {
        logger.severe(message)
    }

    override fun debug(message: String) {
        logger.log(Level.FINE, "[DEBUG] $message")
    }
}