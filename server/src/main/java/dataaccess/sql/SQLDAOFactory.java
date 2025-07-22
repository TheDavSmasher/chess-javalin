package dataaccess.sql;

import dataaccess.DAOFactory;
import dataaccess.DataAccessException;
import dataaccess.DataAccessObject.*;

import java.util.concurrent.atomic.AtomicBoolean;

public class SQLDAOFactory extends DAOFactory {
    private final AtomicBoolean authCreated = new AtomicBoolean();
    private final AtomicBoolean userCreated = new AtomicBoolean();
    private final AtomicBoolean gameCreated = new AtomicBoolean();

    @Override
    protected AuthDAO getNewAuthDAO() throws DataAccessException {
        return new SQLAuthDAO(authCreated);
    }

    @Override
    protected UserDAO getNewUserDAO() throws DataAccessException {
        return new SQLUserDAO(userCreated);
    }

    @Override
    protected GameDAO getNewGameDAO() throws DataAccessException {
        return new SQLGameDAO(gameCreated);
    }
}
