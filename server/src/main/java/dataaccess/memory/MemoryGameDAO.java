package dataaccess.memory;

import model.dataaccess.GameData;

import java.util.ArrayList;
import java.util.Comparator;
import dataaccess.DataAccessObject.*;

public class MemoryGameDAO extends MemoryDAO<GameData> implements GameDAO {
    @Override
    public ArrayList<GameData> listGames() {
        ArrayList<GameData> gameList = new ArrayList<>();
        for (GameData game : data) {
            gameList.add(game.forList());
        }
        gameList.sort(Comparator.comparingInt(GameData::gameID));
        return gameList;
    }

    @Override
    public GameData getGame(int gameID) {
        return get(GameData::gameID, gameID);
    }

    @Override
    public GameData createGame(String gameName) {
        return add(GameData.createNew(data.size() + 1, gameName));
    }

    @Override
    public void updateGamePlayer(int gameID, String color, String username) {
        GameData oldGame = getGame(gameID);
        deleteGame(gameID);
        if (color.equalsIgnoreCase("WHITE")) {
            add(new GameData(oldGame.gameID(), username, oldGame.blackUsername(), oldGame.gameName(), oldGame.game()));
        } else {
            add(new GameData(oldGame.gameID(), oldGame.whiteUsername(), username, oldGame.gameName(), oldGame.game()));
        }
    }

    @Override
    public void updateGameBoard(int gameID, String gameJson) {

    }

    private void deleteGame(int gameID) {
        remove(GameData::gameID, gameID);
    }
}
