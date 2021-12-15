package xyz.theprogramsrc.simplecoreapi.global.module

import java.io.File

/**
 * Representation of a Module
 */
open class Module {
    private lateinit var file: File
    private lateinit var moduleDescription: ModuleDescription

    /**
     * Initializer of the Module (DO NOT CALL FROM EXTERNAL PLUGINS, IT MAY CRASH)
     */
    fun init(file: File, moduleDescription: ModuleDescription) {
        this.file = file
        this.moduleDescription = moduleDescription
    }

    /**
     * Gets the file containing the module
     * @return the file containing the module
     */
    fun getFile(): File = file

    /**
     * Gets the Module Description
     * @return ModuleDescription of the module
     */
    fun getModuleDescription(): ModuleDescription = moduleDescription

    /**
     * Gets the name of the Module
     * @return the name of the module
     */
    fun getName(): String = moduleDescription.name

    /**
     * Gets the version of the Module
     * @return the version of the module
     */
    fun getVersion(): String = moduleDescription.version

    /**
     * Gets the author of the Module
     * @return the author of the module
     */
    fun getAuthor(): String = moduleDescription.author

    open fun onEnable(){

    }

    open fun onDisable(){

    }

}