package client.states;

import java.io.IOException;
import java.io.PrintStream;
import java.util.function.Supplier;

import backend.ServerFacade;
import backend.ServerMessageObserver;
import chess.ChessGame;
import chess.ChessPosition;
import client.ChessClient;
import client.states.ClientCommandProcessing.*;
import ui.ChessUI;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.SET_TEXT_COLOR_RED;
import static utils.Serializer.deserialize;

public class InGameClientState extends AuthorizedClientState implements ServerMessageObserver {
    protected final ClientCommand redraw =
            new ClientCommand(this::redrawBoard, "Redraw Board",
            "print the board again for the current state of the game.");
    protected final ClientCommand highlight =
            new ClientCommand(this::highlightMoves, "Highlight Legal Moves", 1,
            "Please provide a start position.", "start        Format positions column then row, such as G6.",
            "select a position on the board to see all legal moves the piece in that position can make.");
    protected final ClientCommand leave =
            new ClientCommand(this::leaveGame, "Leave",
            "leave the current game, emptying your position and allowing anyone to join. Join again to continue.");

    private final ClientCommand[] stateCommands = { redraw, highlight, leave };

    protected final Supplier<Integer> currentGameID;
    protected final Supplier<Boolean> whitePlayer;

    private ChessGame currentGame = null;
    private final ChessUI chessUI;

    public InGameClientState(
            ServerFacade serverFacade, PrintStream out, ClientChanger client,
            Supplier<String> authToken, Supplier<Integer> currentGameID, Supplier<Boolean> whitePlayer) {
        super(serverFacade, out, client, authToken);
        this.currentGameID = currentGameID;
        this.whitePlayer = whitePlayer;
        this.chessUI = new ChessUI(out);
    }

    @Override
    protected ClientCommand[] getStateCommands() {
        return stateCommands;
    }

    private void redrawBoard() {
        chessUI.printChessBoard(currentGame, null, whitePlayer.get());
    }

    private void highlightMoves(String[] params) throws IOException {
        String startPos = params[0];
        ChessPosition start = positionFromString(startPos);
        chessUI.printChessBoard(currentGame, start, whitePlayer.get());
    }

    private void leaveGame() throws IOException {
        serverFacade.leaveGame(authToken.get(), currentGameID.get());
        currentGame = null;
        client.changeTo(ChessClient.MenuState.POST_LOGIN);
    }

    protected ChessPosition positionFromString(String moveString) throws IOException {
        if (moveString.length() != 2) {
            throw new IOException("Wrong move format!");
        }
        ChessPosition position = new ChessPosition(
                Character.getNumericValue(moveString.charAt(1)),
                Character.getNumericValue(moveString.charAt(0)) - 9 //A is 10
        );
        if (position.outOfBounds()) {
            throw new IOException("Position is invalid!");
        }
        return position;
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> displayNotification((Notification) message);
            case ERROR -> displayError((ErrorMessage) message);
            case LOAD_GAME -> loadGame((LoadGameMessage) message);
        }
    }

    private void displayNotification(Notification notification) {
        out.println(notification.getNotification());
    }

    private void displayError(ErrorMessage errorMessage) {
        out.print(SET_TEXT_COLOR_RED);
        out.println(errorMessage.getError());
        chessUI.resetColor();
    }

    private void loadGame(LoadGameMessage message) {
        currentGame = deserialize(message.getGame(), ChessGame.class);
        redrawBoard();
    }
}
