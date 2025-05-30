package backend;

import backend.websocket.ServerMessageObserver;
import backend.websocket.WebsocketCommunicator;
import chess.ChessMove;
import model.dataaccess.GameData;
import model.request.CreateGameRequest;
import model.request.JoinGameRequest;
import model.request.UserEnterRequest;
import model.response.CreateGameResponse;
import model.response.ListGamesResponse;
import model.response.UserEnterResponse;

import java.io.IOException;
import java.util.ArrayList;

public class ServerFacade {
    private WebsocketCommunicator websocket;
    private final HttpCommunicator http;

    public ServerFacade(int port) {
        http = new HttpCommunicator(getUrlString(port));
    }

    public ServerFacade() {
        this(8080);
    }

    public ServerFacade(ServerMessageObserver observer) {
        this();
        websocket = new WebsocketCommunicator(getUrlString(8080), observer);
    }

    private static String getUrlString(int port) {
        return "http://localhost:" + port + "/";
    }

    public UserEnterResponse register(String username, String password, String email) throws IOException {
        return http.doPost("user", new UserEnterRequest(username, password, email), UserEnterResponse.class);
    }

    public UserEnterResponse login(String username, String password) throws IOException {
        return http.doPost("session", new UserEnterRequest(username, password), UserEnterResponse.class);
    }

    public ArrayList<GameData> listGames(String authToken) throws IOException {
        return http.doGet("game", authToken, ListGamesResponse.class).games();
    }

    public CreateGameResponse createGame(String authToken, String gameName) throws IOException {
        return http.doPost("game", new CreateGameRequest(gameName), authToken, CreateGameResponse.class);
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
    public void connectToGame(String authToken, int gameID) throws IOException {
        websocket.connectToGame(authToken, gameID);
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws IOException {
        websocket.makeMove(authToken, gameID, move);
    }

    public void leaveGame(String authToken, int gameID) throws IOException {
        websocket.leaveGame(authToken, gameID);
    }

    public void resignGame(String authToken, int gameID) throws IOException {
        websocket.resignGame(authToken, gameID);
    }
}
