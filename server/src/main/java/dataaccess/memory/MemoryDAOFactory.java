package dataaccess.memory;

import dataaccess.DAOFactory;
import dataaccess.DataAccessObject.*;

public class MemoryDAOFactory extends DAOFactory {
    @Override
    protected AuthDAO getNewAuthDAO() {
        return new MemoryAuthDAO();
    }

    @Override
    protected UserDAO getNewUserDAO() {
        return new MemoryUserDAO();
    }

    @Override
    protected GameDAO getNewGameDAO() {
        return new MemoryGameDAO();
    }
}
