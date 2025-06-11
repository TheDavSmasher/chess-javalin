package service;

import dataaccess.DataAccessException;
import dataaccess.DataAccessObject.*;
import dataaccess.memory.*;
import dataaccess.sql.*;
import org.eclipse.jetty.util.StringUtil;

import java.util.Arrays;

import static utils.Catcher.*;

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

    //region Interfaces
    protected interface EndpointSupplier<T> {
        T method() throws ServiceException, DataAccessException;
    }

    public interface AuthorizedSupplier<T> {
        T call() throws ServiceException, DataAccessException;
    }

    public interface AuthorizedConsumer {
        void call(String username) throws ServiceException, DataAccessException;
    }
    //endregion

    //region Try Wrappers
    protected static <T> T tryCatch(EndpointSupplier<T> call) throws ServiceException {
        return tryCatchRethrow(call::method, DataAccessException.class, ServiceException.class, UnexpectedException.class);
    }

    public static <T> T tryAuthorized(String authToken, AuthorizedSupplier<T> logic) throws ServiceException {
        return tryCatch(() -> {
            UserService.validateAuth(authToken);
            return logic.call();
        });
    }

    public static <T> T tryAuthorized(String authToken, AuthorizedConsumer logic) throws ServiceException {
        return tryCatch(() -> {
            logic.call(UserService.validateAuth(authToken));
            return null;
        });
    }
    //endregion

    protected static String getValidParameters(String... params) throws BadRequestException {
        return Arrays.stream(params).anyMatch(StringUtil::isBlank) ? throwBadRequest() : params[0];
    }

    protected static <T> T throwUnauthorized() throws UnauthorizedException {
        throw new UnauthorizedException();
    }

    protected static <T> T throwPreexisting() throws PreexistingException {
        throw new PreexistingException();
    }

    protected static <T> T throwBadRequest() throws BadRequestException {
        throw new BadRequestException();
    }
}
