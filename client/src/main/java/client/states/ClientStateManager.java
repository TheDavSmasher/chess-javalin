package client.states;

import backend.ServerFacade;
import client.ClientException;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Optional;

public class ClientStateManager {
    public enum MenuState {
        PRE_LOGIN,
        POST_LOGIN,
        MID_GAME
    }

    //Utils
    public final ServerFacade serverFacade = new ServerFacade();
    public final PrintStream out;
    private final ClientStateFactory clientStates;

    //State variables
    private String authToken = null;
    private int currentGameID = 0;
    private Boolean isPlayerAndWhite = null;
    private MenuState currentState = null;

    public ClientStateManager(PrintStream out) {
        this.out = out;
        this.clientStates = new ClientStateFactory(this);
    }

    //Repl
    public void evaluate(int command, String[] params) throws ClientException, IOException {
        getCurrentState().evaluate(command - 1, params);
    }

    public void help(boolean simple) {
        getCurrentState().help(simple);
    }

    //Getters
    public String getAuthToken() {
        return authToken;
    }

    public int getCurrentGameID() {
        return currentGameID;
    }

    public Optional<Boolean> getIsPlayerAndWhite() {
        return Optional.ofNullable(isPlayerAndWhite);
    }

    //Transitions
    public void enterGame(int gameID, String color) {
        currentGameID = gameID;
        isPlayerAndWhite = color != null ? color.equalsIgnoreCase("white") : null;
        changeState(MenuState.MID_GAME);
    }

    public void returnFromGame() {
        currentGameID = 0;
        isPlayerAndWhite = null;
        changeState(MenuState.POST_LOGIN);
    }

    public void enterServer(String authToken) {
        this.authToken = authToken;
        changeState(MenuState.POST_LOGIN);
    }

    public void logout() {
        this.authToken = null;
        changeState(MenuState.PRE_LOGIN);
    }

    //Helper methods
    public void changeState(MenuState state) {
        currentState = state;
        getCurrentState().help(false);
    }

    private ChessClientState getCurrentState() {
        return clientStates.get(currentState);
    }
}
