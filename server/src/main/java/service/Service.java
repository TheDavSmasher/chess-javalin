package service;

import dataaccess.DataAccessException;
import dataaccess.DataAccessObject;
import dataaccess.memory.*;
import dataaccess.sql.*;

public abstract class Service {
    protected static DataAccessObject.AuthDAO authDAO() throws DataAccessException {
        return DataAccessObject.UseSQL ? SQLAuthDAO.getInstance() : MemoryAuthDAO.getInstance();
    }

    protected static DataAccessObject.UserDAO userDAO() throws DataAccessException {
        return DataAccessObject.UseSQL ? SQLUserDAO.getInstance() : MemoryUserDAO.getInstance();
    }

    protected static DataAccessObject.GameDAO gameDAO() throws DataAccessException {
        return DataAccessObject.UseSQL ? SQLGameDAO.getInstance() : MemoryGameDAO.getInstance();
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
