package xyz.theprogramsrc.simplecoreapi.global.utils.logger

import org.slf4j.Logger
import xyz.theprogramsrc.simplecoreapi.global.utils.ILogger

class SLF4JLogger(val logger: Logger): ILogger {

    override fun info(message: String) {
        logger.info(message)
    }

    override fun warn(message: String) {
        logger.warn(message)
    }

    override fun error(message: String) {
        logger.error(message)
    }

    override fun debug(message: String) {
        logger.debug(message)
    }
}