package service;

import dataaccess.DAOFactory;
import dataaccess.DataAccessException;
import dataaccess.DataAccessObject.*;
import dataaccess.memory.*;
import dataaccess.sql.*;
import model.dataaccess.AuthData;
import org.eclipse.jetty.util.StringUtil;

import java.util.Arrays;

import static utils.Catcher.*;

public abstract class Service {
    //region DAO access
    protected final DAOFactory daoFactory;

    public Service(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    protected AuthDAO authDAO() throws DataAccessException {
        return daoFactory.getAuthDAO();
    }

    protected UserDAO userDAO() throws DataAccessException {
        return daoFactory.getUserDAO();
    }

    protected GameDAO gameDAO() throws DataAccessException {
        return daoFactory.getGameDAO();
    }
    //endregion

    //region Interfaces
    @FunctionalInterface
    protected interface EndpointSupplier<T> {
        T method() throws ServiceException, DataAccessException;
    }

    @FunctionalInterface
    public interface AuthorizedSupplier<T> {
        T call() throws ServiceException, DataAccessException;
    }

    @FunctionalInterface
    public interface AuthorizedConsumer {
        void call(String username) throws ServiceException, DataAccessException;
    }
    //endregion

    //region Try Wrappers
    protected <T> T tryCatch(EndpointSupplier<T> call) throws ServiceException {
        return tryCatchRethrow(call::method, DataAccessException.class, ServiceException.class, UnexpectedException.class);
    }

    public <T> T tryAuthorized(String authToken, AuthorizedSupplier<T> logic) throws ServiceException {
        return tryCatch(() -> {
            validateAuth(authToken);
            return logic.call();
        });
    }

    public <T> T tryAuthorized(String authToken, AuthorizedConsumer logic) throws ServiceException {
        return tryCatch(() -> {
            logic.call(validateAuth(authToken));
            return null;
        });
    }
    //endregion

    protected String getValidParameters(String... params) throws BadRequestException {
        return Arrays.stream(params).anyMatch(StringUtil::isBlank) ? throwNew(BadRequestException.class) : params[0];
    }

    public String validateAuth(String authToken) throws ServiceException {
        return tryCatch(() -> authDAO().getAuth(authToken) instanceof AuthData auth
                ? auth.username() : throwNew(UnauthorizedException.class));
    }
}
