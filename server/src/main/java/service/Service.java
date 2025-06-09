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

    protected interface EndpointSupplier<T> {
        T method() throws ServiceException, DataAccessException;
    }

    protected static <T> T throwUnauthorized() throws UnauthorizedException {
        throw new UnauthorizedException();
    }

    protected static <T> T throwBadRequest() throws BadRequestException {
        throw new BadRequestException();
    }
}
