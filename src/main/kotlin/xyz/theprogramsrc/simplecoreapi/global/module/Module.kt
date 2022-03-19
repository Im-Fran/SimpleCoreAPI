package xyz.theprogramsrc.simplecoreapi.global.module

import java.io.File

/**
 * Representation of a Module
 */
open class Module {
    private lateinit var file: File
    private lateinit var moduleDescription: ModuleDescription
    private var enabled: Boolean = false
    private var loaded: Boolean = false

    /**
     * Initializer of the Module (DO NOT CALL FROM EXTERNAL PLUGINS, IT MAY CRASH)
     */
    fun init(file: File, moduleDescription: ModuleDescription) {
        this.file = file
        this.moduleDescription = moduleDescription
        this.onLoad()
        this.loaded = true
    }

    /**
     * Marks this module as enabled
     */
    fun enable(){
        if(enabled) {
            throw IllegalStateException("Module is already enabled")
        }
        enabled = true
        this.onEnable()
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

    /**
     * Checks if the module is enabled
     * @return true if the module is enabled, false otherwise
     */
    fun isEnabled(): Boolean = enabled

    /**
     * Checks if the module is loaded
     * @return true if the module is loaded, false otherwise
     */
    fun isLoaded(): Boolean = loaded

    open fun onLoad() {

    }

    open fun onEnable(){

    }

    open fun onDisable(){

    }

}