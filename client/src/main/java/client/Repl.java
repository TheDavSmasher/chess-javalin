package client;

import client.states.ClientStateManager.MenuState;
import ui.EscapeSequences.Color;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static utils.Catcher.*;

public class Repl {
    private final PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    private final ChessClient client = new ChessClient(out);

    public void run() {
        out.println("Welcome to my Chess Server!");
        client.changeState(MenuState.PRE_LOGIN);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            printPrompt(out);

            tryCatchDo(() -> tryCatchDo(
                () -> client.evaluate(scanner.nextLine()),
            ExitException.class, _ -> {
                out.println();
                System.exit(0);
            }, Throwable.class), Throwable.class,
                e -> out.print(e.getMessage())
            );
        }
    }

    private void printPrompt(PrintStream out) {
        out.print("\n" + Color.reset(false) + ">>> ");
    }
}
