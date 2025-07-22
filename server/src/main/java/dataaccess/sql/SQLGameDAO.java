package dataaccess.sql;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.dataaccess.GameData;
import dataaccess.DataAccessObject.*;
import org.intellij.lang.annotations.Language;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static utils.Serializer.deserialize;
import static utils.Serializer.serialize;

public class SQLGameDAO extends SQLDAO implements GameDAO {
    public SQLGameDAO(AtomicBoolean tableCreated) throws DataAccessException {
        super(tableCreated);
    }

    @Override
    protected String getTableName() {
        return "games";
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        return tryQuery("", rs -> {
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
        tryInsert("gameName, game", updateResKey -> {
            SQLDAO.confirmUpdate(updateResKey);
            id.set(updateResKey);
        }, gameName, serialize(new ChessGame()));
        return GameData.createNew(id.get(), gameName);
    }

    @Override
    public void updateGamePlayer(int gameID, String color, String username) throws DataAccessException {
        updateGame(gameID, (color.equalsIgnoreCase("WHITE") ? "white" : "black") + "Username", username);
    }

    @Override
    public void updateGameBoard(int gameID, String gameJson) throws DataAccessException {
        updateGame(gameID, "game", gameJson);
    }

    private void updateGame(int gameID, @Language("SQL") String column, String value) throws DataAccessException {
        tryUpdate("UPDATE games SET " + column + "=? WHERE gameID=?", SQLDAO::confirmUpdate, value, gameID);
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
}
