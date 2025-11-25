package client.states;

import backend.ServerFacade;
import backend.http.HTTPServerFacade;
import client.exceptions.ClientException;
import utils.EnumObjectFactory;

import java.io.IOException;
import java.io.PrintStream;

public class ClientStateManager extends EnumObjectFactory<ClientStateManager.MenuState, ChessClientState> {
    public enum MenuState {
        PRE_LOGIN,
        POST_LOGIN,
        MID_GAME
    }

    //Utils
    public final ServerFacade serverFacade = new HTTPServerFacade();
    public final PrintStream out;

    //State variables
    public String authToken = null;
    public int currentGameID = 0;
    public Boolean isPlayerAndWhite = null;
    private MenuState currentState = null;

    public ClientStateManager(PrintStream out) {
        super(false);
        this.out = out;
        generateValues();
    }

    public void evaluate(int command, String[] params) throws ClientException, IOException {
        get(currentState).evaluate(command - 1, params);
    }

    public void help(boolean simple) {
        get(currentState).help(simple);
    }

    public void changeState(MenuState state) {
        currentState = state;
        help(false);
    }

    @Override
    protected Class<MenuState> getKeyClass() {
        return MenuState.class;
    }

    @Override
    protected ChessClientState generateValue(MenuState key) {
        return switch (key) {
            case PRE_LOGIN  -> new PreLoginClientState(this);
            case POST_LOGIN -> new PostLoginClientState(this);
            case MID_GAME   -> new InGameClientState(this);
        };
    }
}
