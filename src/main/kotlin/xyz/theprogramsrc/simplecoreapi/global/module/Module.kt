package xyz.theprogramsrc.simplecoreapi.global.module

import java.io.File

open class Module {
    private lateinit var file: File
    private lateinit var moduleDescription: ModuleDescription

    final fun init(file: File, moduleDescription: ModuleDescription) {
        this.file = file
        this.moduleDescription = moduleDescription
    }

    fun getFile(): File = file

    fun getModuleDescription(): ModuleDescription = moduleDescription

    fun getName(): String = moduleDescription.name

    fun getVersion(): String = moduleDescription.version

    fun getAuthor(): String = moduleDescription.author

    open fun onEnable(){

    }

    open fun onDisable(){

    }

}