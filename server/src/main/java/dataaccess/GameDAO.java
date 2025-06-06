package dataaccess;

import dataaccess.memory.MemoryGameDAO;
import dataaccess.sql.SQLGameDAO;
import model.dataaccess.GameData;
import service.Service;

import java.util.ArrayList;

public interface GameDAO extends ChessDAO {
    ArrayList<GameData> listGames() throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    GameData createGame(String gameName) throws DataAccessException;
    void updateGamePlayer(int gameID, String color, String username) throws DataAccessException;
    void updateGameBoard(int gameID, String gameJson) throws DataAccessException;
    static GameDAO getInstance() throws DataAccessException {
        return Service.UseSQL ? SQLGameDAO.getInstance() : MemoryGameDAO.getInstance();
    }
}
