package client;

import chess.ChessMove;
import client.websocket.ServerMessageObserver;
import client.websocket.WebsocketCommunicator;
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
    private final String urlPort;
    private WebsocketCommunicator websocket;
    private final HttpCommunicator http;

    public ServerFacade(int port) {
        urlPort = "http://localhost:" + port + "/";
        http = new HttpCommunicator(urlPort);
    }

    public ServerFacade() {
        this(8080);
    }

    public ServerFacade(ServerMessageObserver observer) {
        this();
        websocket = new WebsocketCommunicator(urlPort, observer);
    }

    public UserEnterResponse register(String username, String password, String email) throws IOException {
        String url = "user";
        return http.doPost(url,
                new UserEnterRequest(username, password, email), null, UserEnterResponse.class);
    }

    public UserEnterResponse login(String username, String password) throws IOException {
        String url = "session";
        return http.doPost(url, new UserEnterRequest(username, password, null), null, UserEnterResponse.class);
    }

    public ArrayList<GameData> listGames(String authToken) throws IOException {
        ListGamesResponse response = http.doGet("game", authToken, ListGamesResponse.class);
        return response.games();
    }

    public CreateGameResponse createGame(String authToken, String gameName) throws IOException {
        String url = "game";
        return http.doPost(url, new CreateGameRequest(gameName), authToken, CreateGameResponse.class);
    }

    public void joinGame(String authToken, String color, int gameID) throws IOException {
        String url = "game";
        http.doPut(url, new JoinGameRequest(color, gameID), authToken);
    }

    public void logout(String authToken) throws IOException {
        String url = "session";
        http.doDelete(url, authToken);
    }

    public void clear() throws IOException {
        String url = "db";
        http.doDelete(url, null);
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
