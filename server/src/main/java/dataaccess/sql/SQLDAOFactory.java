package dataaccess.sql;

import dataaccess.DAOFactory;
import dataaccess.DataAccessException;
import dataaccess.DataAccessObject.*;

public class SQLDAOFactory extends DAOFactory {
    @Override
    protected AuthDAO getNewAuthDAO() throws DataAccessException {
        return new SQLAuthDAO();
    }

    @Override
    protected UserDAO getNewUserDAO() throws DataAccessException {
        return new SQLUserDAO();
    }

    @Override
    protected GameDAO getNewGameDAO() throws DataAccessException {
        return new SQLGameDAO();
    }
}
