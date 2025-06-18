package client.states;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Optional;
import java.util.function.Supplier;

import backend.ServerFacade;
import backend.ServerMessageObserver;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ChessClient;
import client.states.ClientCommandProcessing.*;
import ui.ChessUI;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.SET_TEXT_COLOR_RED;
import static utils.Catcher.tryCatchRethrow;
import static utils.Serializer.deserialize;

public class InGameClientState extends AuthorizedClientState implements ServerMessageObserver {
    private static final String moveFormat = "        Format positions column then row, such as G6.";

    private final ClientCommand[] observerCommands = {
            new ClientCommand(this::redrawBoard, "Redraw Board",
                    "print the board again for the current state of the game."),
            new ClientCommand(this::highlightMoves, "Highlight Legal Moves", 1,
                    "Please provide a start position.", "start" + moveFormat,
                    "select a position on the board to see all legal moves the piece in that position can make."),
            new ClientCommand(this::leaveGame, "Leave",
                    "leave the current game, emptying your position and allowing anyone to join.",
                    "Join again to continue.")
    };

    private final ClientCommand[] playerCommands = {
            observerCommands[0],
            new ClientCommand(this::makeMove, "Make Move", 2, 3,
                    """
                    Please provide a start and end position.
                    If a pawn is to be promoted, also provide what it should become.
                    ""","start end (pieceType)" + moveFormat,
                    "select a piece in a given position and give its ending position.",
                    "Please make sure the move is legal."),
            observerCommands[1],
            observerCommands[2],
            new ClientCommand(this::resignGame, "Resign",
                    "forfeit the current game, rendering it unplayable and making the opposing player as winner.",
                    "This action cannot be undone after you confirm.")
    };

    protected final Supplier<Integer> currentGameID;
    protected final Supplier<Optional<Boolean>> whitePlayer;

    private ChessGame currentGame = null;
    private final ChessUI chessUI;

    public InGameClientState(
            ServerFacade serverFacade, PrintStream out, ClientChanger client,
            Supplier<String> authToken, Supplier<Integer> currentGameID, Supplier<Optional<Boolean>> whitePlayer) {
        super(serverFacade, out, client, authToken);
        this.currentGameID = currentGameID;
        this.whitePlayer = whitePlayer;
        this.chessUI = new ChessUI(this.out);
        this.serverFacade.registerObserver(this);
    }

    @Override
    protected ClientCommand[] getStateCommands() {
        return whitePlayer.get().isEmpty() ? observerCommands : playerCommands;
    }

    private void redrawBoard() {
        chessUI.printChessBoard(currentGame, null, whitePlayer.get().orElse(true));
    }

    private void makeMove(String[] params) throws IOException {
        String start = params[0], end = params[1];
        ChessPiece.PieceType type = params.length < 3 ? null : typeFromString(params[2]);
        ChessMove move = new ChessMove(positionFromString(start), positionFromString(end), type);
        serverFacade.makeMove(authToken.get(), currentGameID.get(), move);
    }

    private void highlightMoves(String[] params) throws IOException {
        String startPos = params[0];
        ChessPosition start = positionFromString(startPos);
        chessUI.printChessBoard(currentGame, start, whitePlayer.get().orElse(true));
    }

    private void leaveGame() throws IOException {
        serverFacade.leaveGame(authToken.get(), currentGameID.get());
        currentGame = null;
        client.changeTo(ChessClient.MenuState.POST_LOGIN);
    }

    private void resignGame() throws IOException {
        //Add prompt
        serverFacade.resignGame(authToken.get(), currentGameID.get());
    }

    private ChessPiece.PieceType typeFromString(String type) throws IOException {
        return tryCatchRethrow(() -> ChessPiece.PieceType.valueOf(type),
                IllegalArgumentException.class, IOException.class, _ -> "That Piece Type does not exist.");
    }

    private ChessPosition positionFromString(String moveString) throws IOException {
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
