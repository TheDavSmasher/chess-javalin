package dataaccess.sql;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.dataaccess.GameData;
import dataaccess.DataAccessObject.*;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static model.Serializer.deserialize;
import static model.Serializer.serialize;

public class SQLGameDAO extends SQLDAO implements GameDAO {
    private static SQLGameDAO instance;
    private static boolean tableCreated = false;

    private SQLGameDAO() throws DataAccessException {
        super(tableCreated);
        tableCreated = true;
    }

    @Override
    protected String getTableName() {
        return "games";
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        return tryQuery("SELECT gameID, whiteUsername, blackUsername, gameName FROM games", rs -> {
            ArrayList<GameData> gameList = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("gameID");
                String white = rs.getString("whiteUsername");
                String black = rs.getString("blackUsername");
                String name = rs.getString("gameName");

                gameList.add(GameData.forList(id, white, black, name));
            }
            return gameList;
        });
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return trySingleQuery("gameID", gameID, rs -> new GameData(
                rs.getInt("gameID"),
                rs.getString("whiteUsername"),
                rs.getString("blackUsername"),
                rs.getString("gameName"),
                deserialize(rs.getString("game"), ChessGame.class))
        );
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        AtomicInteger id = new AtomicInteger();
        tryUpdate("INSERT INTO games (gameName, game) VALUES (?, ?)", updateResKey -> {
            SQLDAO.confirmUpdate(updateResKey);
            id.set(updateResKey);
        }, gameName, serialize(new ChessGame()));
        return GameData.createNew(id.get(), gameName);
    }

    @Override
    public void updateGamePlayer(int gameID, String color, String username) throws DataAccessException {
        tryUpdate("UPDATE games SET " + (color.equalsIgnoreCase("WHITE") ? "white" : "black")
                        + "Username=? WHERE gameID=?", SQLDAO::confirmUpdate, username, gameID);
    }

    @Override
    public void updateGameBoard(int gameID, String gameJson) throws DataAccessException {
        tryUpdate("UPDATE games SET game=? WHERE gameID=?", SQLDAO::confirmUpdate, gameJson, gameID);
    }

    @Override
    protected String getTableSetup() {
        return """
                (
                  `gameID` int NOT NULL AUTO_INCREMENT,
                  `whiteUsername` varchar(255),
                  `blackUsername` varchar(255),
                  `gameName` varchar(255) NOT NULL,
                  `game` TEXT NOT NULL,
                  PRIMARY KEY (`gameID`),
                  INDEX (`gameName`)
                )
                """;
    }

    public static GameDAO getInstance() throws DataAccessException {
        return instance == null ? (instance = new SQLGameDAO()) : instance;
    }
}
