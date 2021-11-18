package xyz.theprogramsrc.simplecoreapi.global.object;

import java.io.File;

public class Module {

    private final File file;
    private final ModuleDescription description;

    public Module(File file, ModuleDescription description){
        this.file = file;
        this.description = description;
    }

    public ModuleDescription getDescription() {
        return this.description;
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return description.getName();
    }

    public String getVersion() {
        return description.getVersion();
    }

    public String getAuthor() {
        return description.getAuthor();
    }
    
    public void onEnable(){

    }

    public void onDisable(){

    }
}
