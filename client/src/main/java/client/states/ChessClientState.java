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
            help(false);
            return;
        }
        getStateCommands()[command - 1].process(params, command - 1);
    }

    public void help(boolean simple) {
        out.println();

        StringBuilder help = new StringBuilder();
        for (int i = 0; i < getStateCommands().length; i++) {
            help.append(getStateCommands()[i].getHelp(i, simple));
        }
        out.print(help.append("\n").append(helpCommand.getHelp(0, simple)));
    }

    protected abstract ClientCommand[] getStateCommands();

    private final ClientCommand helpCommand = new ClientCommand(() -> help(false), "0 - Help",
            "print this menu again. Also prints out if input is beyond what's accepted.");
}
