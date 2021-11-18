package xyz.theprogramsrc.simplecoreapi.global.exceptions;

public class InvalidModuleException extends RuntimeException {

    public InvalidModuleException(String message) {
        super(message);
    }

    public InvalidModuleException(String message, Throwable e) {
        super(message, e);
    }
    
}
