package xyz.theprogramsrc.simplecoreapi.global.utils

interface ILogger {

    fun info(message: String)

    fun warn(message: String)

    fun error(message: String)

    fun debug(message: String)
}