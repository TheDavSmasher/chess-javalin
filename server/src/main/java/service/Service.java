package service;

import dataaccess.DataAccessException;

public final class Service {
    public static <T> T tryCatch(EndpointSupplier<T> call) throws ServiceException {
        try {
            return call.method();
        } catch (DataAccessException e) {
            throw new UnexpectedException(e.getMessage());
        }
    }

    public static void tryCatch(EndpointRunnable call) throws ServiceException {
        tryCatch(() -> {
            call.method();
            return null;
        });
    }

    public static <T> T throwUnauthorized() throws UnauthorizedException {
        throw new UnauthorizedException();
    }

    public static <T> T throwBadRequest() throws BadRequestException {
        throw new BadRequestException();
    }

    public static <T> T tryAuthorized(String authToken, AuthorizedFunction<T> logic) throws ServiceException {
        return tryCatch(() -> logic.call(UserService.validateAuth(authToken)));
    }

    public static void tryAuthorized(String authToken, AuthorizedConsumer logic) throws ServiceException {
        tryCatch(() -> logic.call(UserService.validateAuth(authToken)));
    }

    //region Interfaces
    public interface EndpointSupplier<T> {
        T method() throws ServiceException, DataAccessException;
    }

    public interface EndpointRunnable {
        void method() throws ServiceException, DataAccessException;
    }

    public interface AuthorizedFunction<T> {
        T call(String username) throws ServiceException, DataAccessException;
    }

    public interface AuthorizedConsumer {
        void call(String username) throws ServiceException, DataAccessException;
    }
    //endregion
}
