package xyz.theprogramsrc.simplecoreapi.global.exceptions

class InvalidModuleDescriptionException: RuntimeException {
    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable): super(message, cause)
}