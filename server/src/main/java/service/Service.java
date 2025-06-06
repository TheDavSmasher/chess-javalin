package service;

import dataaccess.DataAccessException;

public final class Service {
    public interface EndpointCall<T> {
        T method() throws ServiceException, DataAccessException;
    }

    public static <T> T tryCatch(EndpointCall<T> call) throws ServiceException {
        try {
            return call.method();
        } catch (DataAccessException e) {
            throw new UnexpectedException(e.getMessage());
        }
    }

    public static <T> T throwUnauthorized() throws UnauthorizedException {
        throw new UnauthorizedException();
    }

    public static <T> T throwBadRequest() throws BadRequestException {
        throw new BadRequestException();
    }

    public interface AuthorizedCall<T> {
        T call(String username) throws ServiceException, DataAccessException;
    }

    public static <T> T tryAuthorized(String authToken, AuthorizedCall<T> logic) throws ServiceException {
        return tryCatch(() -> logic.call(UserService.validateAuth(authToken)));
    }
}
