package client.states;

import backend.ServerFacade;

import java.io.PrintStream;
import java.util.function.Supplier;

public abstract class AuthorizedClientState extends ChessClientState {
    protected final Supplier<String> authToken;

    protected AuthorizedClientState(
            ServerFacade serverFacade, PrintStream out, ClientChanger client, Supplier<String> authToken) {
        super(serverFacade, out, client);
        this.authToken = authToken;
    }
}
