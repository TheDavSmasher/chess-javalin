package service;

public abstract class ServiceException extends Exception {
    protected ServiceException(String message) {
        super(message);
    }
}
