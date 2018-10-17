package Exceptions;

public class FileUtilityException extends Exception{

    public FileUtilityException(String msg) {
        super(msg);
    }

    public FileUtilityException(Throwable throwable) {
        super(throwable);
    }

    public FileUtilityException(String message, Throwable e) {
        super(message, e);
    }
}
