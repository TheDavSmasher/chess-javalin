package dataaccess.memory;

import model.dataaccess.GameData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import dataaccess.DataAccessObject.*;

public class MemoryGameDAO implements GameDAO {
    static MemoryGameDAO instance;
    private final HashSet<GameData> data;

    private MemoryGameDAO() {
        data = new HashSet<>();
    }

    @Override
    public ArrayList<GameData> listGames() {
        ArrayList<GameData> gameList = new ArrayList<>();
        for (GameData game : data) {
            gameList.add(GameData.forList(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
        }
        gameList.sort(Comparator.comparingInt(GameData::gameID));
        return gameList;
    }

    @Override
    public GameData getGame(int gameID) {
        return data.stream().filter(g -> g.gameID() == gameID).findFirst().orElse(null);
    }

    @Override
    public GameData createGame(String gameName) {
        int newID = data.size() + 1;
        GameData newGame = GameData.createNew(newID, gameName);
        data.add(newGame);
        return newGame;
    }

    @Override
    public void updateGamePlayer(int gameID, String color, String username) {
        GameData oldGame = getGame(gameID);
        deleteGame(gameID);
        if (color.equalsIgnoreCase("WHITE")) {
            data.add(new GameData(oldGame.gameID(), username, oldGame.blackUsername(), oldGame.gameName(), oldGame.game()));
        } else {
            data.add(new GameData(oldGame.gameID(), oldGame.whiteUsername(), username, oldGame.gameName(), oldGame.game()));
        }
    }

    @Override
    public void updateGameBoard(int gameID, String gameJson) {

    }

    private void deleteGame(int gameID) {
        data.removeIf(game -> game.gameID() == gameID);
    }

    @Override
    public void clear() {
        data.clear();
    }

    public static GameDAO getInstance() {
        return instance == null ? (instance = new MemoryGameDAO()) :  instance;
    }
}
