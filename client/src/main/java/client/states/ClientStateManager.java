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
    public String authToken = null;
    public int currentGameID = 0;
    public Boolean isPlayerAndWhite = null;
    private MenuState currentState = null;

    public ClientStateManager(PrintStream out) {
        this.out = out;
        this.clientStates = new ClientStateFactory(this);
    }

    public void evaluate(int command, String[] params) throws ClientException, IOException {
        getCurrentState().evaluate(command - 1, params);
    }

    public void help(boolean simple) {
        getCurrentState().help(simple);
    }

    public Optional<Boolean> getIsPlayerAndWhite() {
        return Optional.ofNullable(isPlayerAndWhite);
    }

    public void changeState(MenuState state) {
        currentState = state;
        help(false);
    }

    private ChessClientState getCurrentState() {
        return clientStates.get(currentState);
    }
}
