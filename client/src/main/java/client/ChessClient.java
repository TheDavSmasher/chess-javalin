package client;

import backend.*;
import client.states.*;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.EnumMap;

import static utils.Catcher.*;

public class ChessClient {
    public enum MenuState {
        PRE_LOGIN,
        POST_LOGIN,
        MID_GAME
    }

    private final EnumMap<MenuState, ChessClientState> clientStates = new EnumMap<>(MenuState.class);

    private MenuState currentState = MenuState.PRE_LOGIN;

    public ChessClient(PrintStream out) {
        ServerFacade serverFacade = new ServerFacade();
        ClientStateManager clientStateManager = new ClientStateManager(this);
        clientStates.put(MenuState.PRE_LOGIN,
                new PreLoginClientState(serverFacade, out, clientStateManager));
        clientStates.put(MenuState.POST_LOGIN,
                new PostLoginClientState(serverFacade, out, clientStateManager));
        clientStates.put(MenuState.MID_GAME,
                new InGameClientState(serverFacade, out, clientStateManager));
    }

    public void evaluate(String input) throws ClientException {
        String[] tokens = input.toLowerCase().split(" ");
        tryCatchDo(() -> tryCatchRethrow(() -> {
            int command = (tokens.length > 0) ? Integer.parseInt(tokens[0]) : 0;
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            clientStates.get(currentState).evaluate(command, params);
            return null;
        }, IOException.class, ClientException.class),
                NumberFormatException.class, _ -> help(true), ClientException.class);
    }

    public void changeState(MenuState state) {
        currentState = state;
        help(false);
    }

    private void help(boolean simple) {
        clientStates.get(currentState).help(simple);
    }
}
