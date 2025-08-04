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
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setCurrentGameID(int currentGameID) {
        this.currentGameID = currentGameID;
    }

    public int getCurrentGameID() {
        return currentGameID;
    }

    public void setIsPlayerAndWhite(Boolean isPlayerAndWhite) {
        this.isPlayerAndWhite = isPlayerAndWhite;
    }

    public Optional<Boolean> getIsPlayerAndWhite() {
        return Optional.ofNullable(isPlayerAndWhite);
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
