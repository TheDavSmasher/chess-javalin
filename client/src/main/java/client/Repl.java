package client;

import client.states.ClientStateManager;
import ui.EscapeSequences.Format;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

import static utils.Catcher.*;

public class Repl {
    private final PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    private final ClientStateManager clientStates = new ClientStateManager(out);
    private final Scanner scanner = new Scanner(System.in);

    public void run() {
        out.println("Welcome to my Chess Server!\nRun the 'help' command to see available commands.");

        while (true) {
            printPrompt(out);

            tryCatchDo(() -> tryCatchDo(
                () -> evaluate(scanner.nextLine()),
            ExitException.class, _ -> {
                out.println();
                System.exit(0);
            }, Throwable.class), Throwable.class,
                e -> out.print(e.getMessage())
            );
        }
    }

    private void evaluate(String input) throws ClientException {
        String[] tokens = input.toLowerCase().split(" ");
        tryCatchDo(() -> tryCatchRethrow(() -> {
            clientStates.getCurrentState().evaluate(
                    (tokens.length > 0 ? Integer.parseInt(tokens[0]) : 0) - 1,
                    Arrays.copyOfRange(tokens, 1, tokens.length));
            return null;
        }, IOException.class, ClientException.class),
        NumberFormatException.class,
        _ -> clientStates.getCurrentState().help(true),
        ClientException.class);
    }

    private void printPrompt(PrintStream out) {
        out.print("\n" + Format.TEXT.reset() + ">>> ");
    }
}
