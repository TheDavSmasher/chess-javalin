package dataaccess;

import dataaccess.DataAccessObject.*;

public abstract class DAOFactory {
    protected AuthDAO authDAO;
    protected UserDAO userDAO;
    protected GameDAO gameDAO;

    protected abstract AuthDAO getNewAuthDAO() throws DataAccessException;
    protected abstract UserDAO getNewUserDAO() throws DataAccessException;
    protected abstract GameDAO getNewGameDAO() throws DataAccessException;

    public AuthDAO getAuthDAO() throws DataAccessException {
        return authDAO == null ? (authDAO = getNewAuthDAO()) : authDAO;
    }

    public UserDAO getUserDAO() throws DataAccessException {
        return userDAO == null ? (userDAO = getNewUserDAO()) : userDAO;
    }

    public GameDAO getGameDAO() throws DataAccessException {
        return gameDAO == null ? (gameDAO = getNewGameDAO()) : gameDAO;
    }
}
