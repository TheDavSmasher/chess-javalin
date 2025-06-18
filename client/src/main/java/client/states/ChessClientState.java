package client.states;

import client.ClientException;
import client.states.ClientCommandProcessing.*;

import java.io.IOException;

public abstract class ChessClientState {
    protected final ClientStateManager client;

    protected ChessClientState(ClientStateManager client) {
        this.client = client;
    }

    public void evaluate(int command, String[] params) throws ClientException, IOException {
        if (command < 0 || command >= getStateCommands().length) {
            help(false);
            return;
        }
        getStateCommands()[command].process(params, command);
    }

    public void help(boolean simple) {
        client.out.println();

        StringBuilder help = new StringBuilder();
        for (int i = 0; i < getStateCommands().length; i++) {
            getStateCommands()[i].getHelp(help, i, simple);
        }
        client.out.print(helpCommand.getHelp(help.append("\n"), 0, simple));
    }

    protected abstract ClientCommand[] getStateCommands();

    private final ClientCommand helpCommand = new ClientCommand(() -> help(false), "Help",
            "print this menu again. Also prints out if input is beyond what's accepted.");
}
