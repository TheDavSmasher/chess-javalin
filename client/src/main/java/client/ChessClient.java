package client;

import client.states.*;
import client.states.ClientStateManager.MenuState;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.EnumMap;

import static utils.Catcher.*;

public class ChessClient {
    }

    private final EnumMap<MenuState, ChessClientState> clientStates = new EnumMap<>(MenuState.class);

    private MenuState currentState = MenuState.PRE_LOGIN;

    public ChessClient(PrintStream out) {
        ClientStateManager clientStateManager = new ClientStateManager(this, out);
        clientStates.put(MenuState.PRE_LOGIN,
                new PreLoginClientState(clientStateManager));
        clientStates.put(MenuState.POST_LOGIN,
                new PostLoginClientState(clientStateManager));
        clientStates.put(MenuState.MID_GAME,
                new InGameClientState(clientStateManager));
    }

    public void evaluate(String input) throws ClientException {
        String[] tokens = input.toLowerCase().split(" ");
        tryCatchDo(() -> tryCatchRethrow(() -> {
            getCurrentState().evaluate(
                    (tokens.length > 0 ? Integer.parseInt(tokens[0]) : 0) - 1,
                    Arrays.copyOfRange(tokens, 1, tokens.length));
            return null;
        }, IOException.class, ClientException.class),
                NumberFormatException.class, _ -> getCurrentState().help(true), ClientException.class);
    }

    public void changeState(MenuState state) {
        currentState = state;
        getCurrentState().help(false);
    }

    private ChessClientState getCurrentState() {
        return clientStates.get(currentState);
    }
}
