package dataaccess;

import dataaccess.memory.MemoryAuthDAO;
import dataaccess.sql.SQLAuthDAO;
import model.dataaccess.AuthData;
import service.Service;

public interface AuthDAO extends ChessDAO {
    AuthData getAuth(String token) throws DataAccessException;
    AuthData createAuth(String username) throws DataAccessException;
    void deleteAuth(String token) throws DataAccessException;
    static AuthDAO getInstance() throws DataAccessException {
        return Service.UseSQL ? SQLAuthDAO.getInstance() : MemoryAuthDAO.getInstance();
    }
}
