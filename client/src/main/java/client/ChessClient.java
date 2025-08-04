package client;

import client.states.*;
import client.states.ClientStateManager.MenuState;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import static utils.Catcher.*;

public class ChessClient {
    private final ClientStateFactory clientStates;

    private MenuState currentState = null;

    public ChessClient(PrintStream out) {
        clientStates = new ClientStateFactory(new ClientStateManager(this, out));
    }

    public void evaluate(String input) throws ClientException {
        String[] tokens = input.toLowerCase().split(" ");
        tryCatchDo(() -> tryCatchRethrow(() -> {
            clientStates.get(currentState).evaluate(
                    (tokens.length > 0 ? Integer.parseInt(tokens[0]) : 0) - 1,
                    Arrays.copyOfRange(tokens, 1, tokens.length));
            return null;
        }, IOException.class, ClientException.class),
        NumberFormatException.class,
            _ -> clientStates.get(currentState).help(true),
        ClientException.class);
    }

    public void changeState(MenuState state) {
        currentState = state;
        clientStates.get(currentState).help(false);
    }
}
