package service.exception;

public class BadRequestException extends ServiceException {
    public BadRequestException() {
        super("bad request");
    }
}
