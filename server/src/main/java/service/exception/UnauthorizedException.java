package service.exception;

public class UnauthorizedException extends ServiceException {
    public UnauthorizedException() {
        super("unauthorized");
    }
}
