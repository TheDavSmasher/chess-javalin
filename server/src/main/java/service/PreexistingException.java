package service;

public class PreexistingException extends ServiceException {
    public PreexistingException() {
        super("already taken");
    }
}
