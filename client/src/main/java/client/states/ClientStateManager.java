package client.states;

import backend.ServerFacade;
import client.ChessClient;

import java.io.PrintStream;
import java.util.Optional;

public class ClientStateManager {
    public enum MenuState {
        PRE_LOGIN,
        POST_LOGIN,
        MID_GAME
    }

    public final ServerFacade serverFacade = new ServerFacade();
    public final PrintStream out;
    private final ChessClient chessClient;

    private String authToken = null;
    private int currentGameID = 0;
    private Boolean isPlayerAndWhite = null;

    public ClientStateManager(ChessClient chessClient, PrintStream out) {
        this.chessClient = chessClient;
        this.out = out;
    }

    public String getAuthToken() {
        return authToken;
    }

    public int getCurrentGameID() {
        return currentGameID;
    }

    public Optional<Boolean> getIsPlayerAndWhite() {
        return Optional.ofNullable(isPlayerAndWhite);
    }

    public void enterGame(int gameID, String color) {
        currentGameID = gameID;
        isPlayerAndWhite = color != null ? color.equalsIgnoreCase("white") : null;
        chessClient.changeState(MenuState.MID_GAME);
    }

    public void returnFromGame() {
        enterServer(authToken);
    }

    public void enterServer(String authToken) {
        this.authToken = authToken;
        currentGameID = 0;
        chessClient.changeState(MenuState.POST_LOGIN);
    }

    public void logout() {
        this.authToken = null;
        chessClient.changeState(MenuState.PRE_LOGIN);
    }
}
