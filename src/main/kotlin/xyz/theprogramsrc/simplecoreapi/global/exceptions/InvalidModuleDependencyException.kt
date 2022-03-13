package xyz.theprogramsrc.simplecoreapi.global.exceptions

class InvalidModuleDependencyException: RuntimeException {
    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable): super(message, cause)
}