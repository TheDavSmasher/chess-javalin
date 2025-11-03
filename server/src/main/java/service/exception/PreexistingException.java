package service.exception;

public class PreexistingException extends ServiceException {
    public PreexistingException() {
        super("already taken");
    }
}
