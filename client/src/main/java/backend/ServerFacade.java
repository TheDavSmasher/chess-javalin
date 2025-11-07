package backend;

import chess.ChessMove;
import model.dataaccess.GameData;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Collection;

public interface ServerFacade {
    String register(String username, String password, String email) throws IOException;
    String login(String username, String password) throws IOException;
    Collection<GameData> listGames(String authToken) throws IOException;
    int createGame(String authToken, String gameName) throws IOException;
    void joinGame(String authToken, String color, int gameID) throws IOException;
    void logout(String authToken) throws IOException;
    void clear() throws IOException;
    void registerObserver(ServerMessageObserver observer);
    void connectToGame(String authToken, int gameID) throws IOException;
    void makeMove(String authToken, int gameID, ChessMove move) throws IOException ;
    void leaveGame(String authToken, int gameID) throws IOException;
    void resignGame(String authToken, int gameID) throws IOException;

    interface ServerMessageObserver {
        void notify(ServerMessage message);
    }
}
