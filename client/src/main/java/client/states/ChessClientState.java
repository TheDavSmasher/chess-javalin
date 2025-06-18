package client.states;

import backend.ServerFacade;
import client.ChessClient;
import client.ClientException;
import client.states.ClientCommandProcessing.*;

import java.io.IOException;
import java.io.PrintStream;

public abstract class ChessClientState {
    @FunctionalInterface
    public interface ClientChanger {
        void changeTo(ChessClient.MenuState target, Object... args);
    }

    protected final ServerFacade serverFacade;
    protected final PrintStream out;
    protected final ClientChanger client;

    protected ChessClientState(ServerFacade serverFacade, PrintStream out, ClientChanger client) {
        this.serverFacade = serverFacade;
        this.out = out;
        this.client = client;
    }

    public void evaluate(int command, String[] params) throws ClientException, IOException {
        if (command < 1 || command > getStateCommands().length) {
            //help(false);
            return;
        }
        getStateCommands()[command - 1].process(params, command - 1);
    }

    //define help command

    protected abstract ClientCommand[] getStateCommands();
}
