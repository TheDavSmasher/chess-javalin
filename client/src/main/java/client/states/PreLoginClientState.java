package client.states;

import backend.ServerFacade;
import client.ExitException;

import java.io.IOException;
import java.io.PrintStream;

import client.states.ClientCommandProcessing.*;

public class PreLoginClientState extends ChessClientState {
    private final ClientCommand[] stateCommands = {
            new ClientCommand(this::register, "Register", 3,
                    "Please provide a username, password, and email.", "username password email",
                    "creates a new user in the database. Username must be unique."),
            new ClientCommand(this::signIn, "Login", 2,
                    "Please provide a username and password", "username password",
                    "logs in to the server with a pre-registered username with its corresponding password."),
            new ClientCommand(this::quit, "Quit",
                    "exit out of the client.")
    };

    public PreLoginClientState(ServerFacade serverFacade, PrintStream out, ClientStateManager client) {
        super(serverFacade, out, client);
    }

    @Override
    protected ClientCommand[] getStateCommands() {
        return stateCommands;
    }

    private void register(String[] params) throws IOException {
        String username = params[0], password = params[1], email = params[2];
        client.enterServer(serverFacade.register(username, password, email).authToken());
    }

    private void signIn(String[] params) throws IOException {
        String username = params[0], password = params[1];
        client.enterServer(serverFacade.login(username, password).authToken());
    }

    private void quit() throws ExitException {
        throw new ExitException();
    }
}
