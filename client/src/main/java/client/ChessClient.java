package client;

import client.states.*;
import client.states.ClientStateManager.MenuState;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import utils.EnumObjectFactory;

import static utils.Catcher.*;

public class ChessClient extends EnumObjectFactory<MenuState, ChessClientState> {
    private final ClientStateManager clientStateManager;

    private MenuState currentState = null;

    public ChessClient(PrintStream out) {
        super(false);
        clientStateManager = new ClientStateManager(this, out);
        generateValues();
    }

    @Override
    protected Class<MenuState> getKeyClass() {
        return MenuState.class;
    }

    @Override
    protected ChessClientState generateValue(MenuState key) {
        return switch (key) {
            case PRE_LOGIN -> new PreLoginClientState(clientStateManager);
            case POST_LOGIN -> new PostLoginClientState(clientStateManager);
            case MID_GAME -> new InGameClientState(clientStateManager);
        };
    }

    public void evaluate(String input) throws ClientException {
        String[] tokens = input.toLowerCase().split(" ");
        tryCatchDo(() -> tryCatchRethrow(() -> {
            get(currentState).evaluate(
                    (tokens.length > 0 ? Integer.parseInt(tokens[0]) : 0) - 1,
                    Arrays.copyOfRange(tokens, 1, tokens.length));
            return null;
        }, IOException.class, ClientException.class),
        NumberFormatException.class,
            _ -> get(currentState).help(true),
        ClientException.class);
    }

    public void changeState(MenuState state) {
        currentState = state;
        get(currentState).help(false);
    }
}
