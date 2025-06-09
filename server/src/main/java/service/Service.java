package service;

import dataaccess.DataAccessException;

public abstract class Service {
    protected static <T> T tryCatch(EndpointSupplier<T> call) throws ServiceException {
        try {
            return call.method();
        } catch (DataAccessException e) {
            throw new UnexpectedException(e.getMessage());
        }
    }

    protected static <T> T tryAuthorized(String authToken, AuthorizedSupplier<T> logic) throws ServiceException {
        return tryCatch(() -> logic.call(UserService.validateAuth(authToken)));
    }

    //region Interfaces
    protected interface EndpointSupplier<T> {
        T method() throws ServiceException, DataAccessException;
    }

    protected interface AuthorizedSupplier<T> {
        T call(String username) throws ServiceException, DataAccessException;
    }
    //endregion

    protected static <T> T throwUnauthorized() throws UnauthorizedException {
        throw new UnauthorizedException();
    }

    protected static <T> T throwBadRequest() throws BadRequestException {
        throw new BadRequestException();
    }
}
