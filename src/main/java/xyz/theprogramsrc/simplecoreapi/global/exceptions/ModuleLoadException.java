package xyz.theprogramsrc.simplecoreapi.global.exceptions;

public class ModuleLoadException extends RuntimeException {

    public ModuleLoadException(String message, Throwable e) {
        super(message, e);
    }
    
}
