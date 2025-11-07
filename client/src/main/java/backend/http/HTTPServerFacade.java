package backend.http;

import backend.ServerFacade;
import chess.ChessMove;
import model.dataaccess.GameData;
import model.request.*;
import model.response.*;
import websocket.commands.*;

import java.io.IOException;
import java.util.Collection;

public class HTTPServerFacade implements ServerFacade {
    private final WebsocketCommunicator websocket;
    private final HttpCommunicator http;

    public HTTPServerFacade() {
        this(8080);
    }

    public HTTPServerFacade(int port) {
        String url = "http://localhost:%d/".formatted(port);
        http = new HttpCommunicator(url);
        websocket = new WebsocketCommunicator(url);
    }

    public String register(String username, String password, String email) throws IOException {
        return http.doPost("user", new UserEnterRequest(username, password, email), UserEnterResponse.class).authToken();
    }

    public String login(String username, String password) throws IOException {
        return http.doPost("session", new UserEnterRequest(username, password), UserEnterResponse.class).authToken();
    }

    public Collection<GameData> listGames(String authToken) throws IOException {
        return http.doGet("game", authToken, ListGamesResponse.class).games();
    }

    public int createGame(String authToken, String gameName) throws IOException {
        return http.doPost("game", new CreateGameRequest(gameName), authToken, CreateGameResponse.class).gameID();
    }

    public void joinGame(String authToken, String color, int gameID) throws IOException {
        http.doPut("game", new JoinGameRequest(color, gameID), authToken);
    }

    public void logout(String authToken) throws IOException {
        http.doDelete("session", authToken);
    }

    public void clear() throws IOException {
        http.doDelete("db");
    }

    //Websocket
    public void registerObserver(ServerMessageObserver observer) {
        websocket.registerObserver(observer);
    }

    public void connectToGame(String authToken, int gameID) throws IOException {
        websocket.sendCommand(new ConnectCommand(authToken, gameID));
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws IOException {
        websocket.sendCommand(new MakeMoveCommand(authToken, gameID, move));
    }

    public void leaveGame(String authToken, int gameID) throws IOException {
        websocket.sendCommand(new LeaveCommand(authToken, gameID));
    }

    public void resignGame(String authToken, int gameID) throws IOException {
        websocket.sendCommand(new ResignCommand(authToken, gameID));
    }
}
