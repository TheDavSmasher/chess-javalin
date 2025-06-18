package client;

import backend.*;
import client.states.*;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.EnumMap;

import static utils.Catcher.*;
import static java.util.Optional.ofNullable;

public class ChessClient {
    private final EnumMap<MenuState, ChessClientState> stateManager = new EnumMap<>(MenuState.class);

    private MenuState currentState = MenuState.PRE_LOGIN;

    private String authToken = null;
    private int currentGameID = 0;
    private Boolean whitePlayer = null;

    public ChessClient(PrintStream out) {
        ServerFacade serverFacade = new ServerFacade();
        stateManager.put(MenuState.PRE_LOGIN,
                new PreLoginClientState(serverFacade, out, this::changeClientState));
        stateManager.put(MenuState.POST_LOGIN,
                new PostLoginClientState(serverFacade, out, this::changeClientState, () -> authToken));
        stateManager.put(MenuState.MID_GAME,
                new InGameClientState(serverFacade, out, this::changeClientState,
                        () -> authToken, () -> currentGameID, () -> ofNullable(whitePlayer)));
    }

    public void evaluate(String input) throws ClientException {
        String[] tokens = input.toLowerCase().split(" ");
        tryCatchDo(() -> tryCatchRethrow(() -> {
            int command = (tokens.length > 0) ? Integer.parseInt(tokens[0]) : 0;
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            stateManager.get(currentState).evaluate(command, params);
            return null;
        }, IOException.class, ClientException.class),
                NumberFormatException.class, _ -> help(true), ClientException.class);
    }

    private void changeClientState(MenuState state, Object... args) {
        currentState = state;
        if (state == MenuState.POST_LOGIN) {
            currentGameID = 0;
            authToken = args[0] instanceof String newAuth ? newAuth : authToken;
        } else if (state == MenuState.PRE_LOGIN) {
            authToken = null;
        } else {
            currentGameID = (int) args[0];
            whitePlayer = args[1] instanceof String color ? color.equalsIgnoreCase("white") : null;
        }
        help(true);
    }

    public enum MenuState {
        PRE_LOGIN,
        POST_LOGIN,
        MID_GAME
    }

    public void help(boolean simple) {
        stateManager.get(currentState).help(simple);
    }
}
