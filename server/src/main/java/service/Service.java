package service;

import dataaccess.DataAccessException;
import dataaccess.DataAccessObject.*;
import dataaccess.memory.*;
import dataaccess.sql.*;

import static org.eclipse.jetty.util.StringUtil.isBlank;

public abstract class Service {
    //region DAO access
    private static final boolean USE_SQL = true;

    protected static AuthDAO authDAO() throws DataAccessException {
        return USE_SQL ? SQLAuthDAO.getInstance() : MemoryAuthDAO.getInstance();
    }

    protected static UserDAO userDAO() throws DataAccessException {
        return USE_SQL ? SQLUserDAO.getInstance() : MemoryUserDAO.getInstance();
    }

    protected static GameDAO gameDAO() throws DataAccessException {
        return USE_SQL ? SQLGameDAO.getInstance() : MemoryGameDAO.getInstance();
    }
    //endregion

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

    protected static String getValidParameters(String... params) throws BadRequestException {
        for (String param : params) {
            if (isBlank(param)) {
                throw new BadRequestException();
            }
        }
        return params[0];
    }

    protected static <T> T throwUnauthorized() throws UnauthorizedException {
        throw new UnauthorizedException();
    }
}
