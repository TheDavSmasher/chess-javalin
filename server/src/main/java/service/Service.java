package service;

import dataaccess.DataAccessException;
import dataaccess.DataAccessObject.*;

public abstract class Service {
    protected static AuthDAO authDAO() throws DataAccessException {
        return AuthDAO.getInstance();
    }

    protected static UserDAO userDAO() throws DataAccessException {
        return UserDAO.getInstance();
    }

    protected static GameDAO gameDAO() throws DataAccessException {
        return GameDAO.getInstance();
    }

    protected interface EndpointSupplier<T> {
        T method() throws ServiceException, DataAccessException;
    }

    protected static <T> T tryCatch(EndpointSupplier<T> call) throws ServiceException {
        try {
            return call.method();
        } catch (DataAccessException e) {
            throw new UnexpectedException(e.getMessage());
        }
    }

    protected static <T> T throwUnauthorized() throws UnauthorizedException {
        throw new UnauthorizedException();
    }

    protected static <T> T throwBadRequest() throws BadRequestException {
        throw new BadRequestException();
    }
}
