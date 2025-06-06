package dataaccess;

import dataaccess.memory.MemoryUserDAO;
import dataaccess.sql.SQLUserDAO;
import model.dataaccess.UserData;
import service.Service;

public interface UserDAO extends ChessDAO {
    UserData getUser(String username) throws DataAccessException;
    UserData getUser(String username, String password) throws DataAccessException;
    void createUser(String username, String password, String email) throws DataAccessException;
    static UserDAO getInstance() throws DataAccessException {
        return Service.UseSQL ? SQLUserDAO.getInstance() : MemoryUserDAO.getInstance();
    }
}
