package xyz.theprogramsrc.simplecoreapi.global.exceptions

class InvalidModuleException: RuntimeException {
    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable): super(message, cause)
}