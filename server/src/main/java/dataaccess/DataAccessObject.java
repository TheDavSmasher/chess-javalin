package dataaccess;

import dataaccess.memory.*;
import dataaccess.sql.*;
import model.dataaccess.*;
import java.util.ArrayList;

public interface DataAccessObject {
    boolean UseSQL = true;

    interface ChessDAO {
        void clear() throws DataAccessException;
    }

    interface AuthDAO extends ChessDAO {
        AuthData getAuth(String token) throws DataAccessException;
        AuthData createAuth(String username) throws DataAccessException;
        void deleteAuth(String token) throws DataAccessException;
        static AuthDAO getInstance() throws DataAccessException {
            return UseSQL ? SQLAuthDAO.getInstance() : MemoryAuthDAO.getInstance();
        }
    }

    interface UserDAO extends ChessDAO {
        UserData getUser(String username) throws DataAccessException;
        UserData getUser(String username, String password) throws DataAccessException;
        void createUser(String username, String password, String email) throws DataAccessException;
        static UserDAO getInstance() throws DataAccessException {
            return UseSQL ? SQLUserDAO.getInstance() : MemoryUserDAO.getInstance();
        }
    }

    interface GameDAO extends ChessDAO {
        ArrayList<GameData> listGames() throws DataAccessException;
        GameData getGame(int gameID) throws DataAccessException;
        GameData createGame(String gameName) throws DataAccessException;
        void updateGamePlayer(int gameID, String color, String username) throws DataAccessException;
        void updateGameBoard(int gameID, String gameJson) throws DataAccessException;
        static GameDAO getInstance() throws DataAccessException {
            return UseSQL ? SQLGameDAO.getInstance() : MemoryGameDAO.getInstance();
        }
    }
}
