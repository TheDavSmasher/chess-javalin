package client.states;

import java.io.IOException;

import backend.ServerMessageObserver;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.states.ClientCommandProcessing.*;
import ui.BoardPrinter;
import ui.ChessUI;
import ui.EscapeSequences.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import static utils.Catcher.tryCatchRethrow;
import static utils.Serializer.deserialize;

public class InGameClientState extends ChessClientState implements ServerMessageObserver {
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

    private ChessGame currentGame = null;
    private final BoardPrinter boardPrinter;

    public InGameClientState(ClientStateManager client) {
        super(client);
        this.boardPrinter = new ChessUI(client.out);
        client.serverFacade.registerObserver(this);
    }

    @Override
    protected ClientCommand[] getStateCommands() {
        return stateManager.getIsPlayerAndWhite().isEmpty() ? observerCommands : playerCommands;
    }

    private void redrawBoard() {
        boardPrinter.printChessBoard(currentGame, null, stateManager.getIsPlayerAndWhite().orElse(true));
    }

    private void makeMove(String[] params) throws IOException {
        String start = params[0], end = params[1];
        ChessPiece.PieceType type = params.length < 3 ? null : typeFromString(params[2]);
        ChessMove move = new ChessMove(positionFromString(start), positionFromString(end), type);
        stateManager.serverFacade.makeMove(stateManager.authToken, stateManager.currentGameID, move);
    }

    private void highlightMoves(String[] params) throws IOException {
        String startPos = params[0];
        ChessPosition start = positionFromString(startPos);
        boardPrinter.printChessBoard(currentGame, start, stateManager.getIsPlayerAndWhite().orElse(true));
    }

    private void leaveGame() throws IOException {
        stateManager.serverFacade.leaveGame(stateManager.authToken, stateManager.currentGameID);
        currentGame = null;
        stateManager.currentGameID = 0;
        stateManager.isPlayerAndWhite = null;
        stateManager.changeState(ClientStateManager.MenuState.POST_LOGIN);
    }

    private void resignGame() throws IOException {
        //Add prompt
        stateManager.serverFacade.resignGame(stateManager.authToken, stateManager.currentGameID);
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
        stateManager.out.println(notification.getNotification());
    }

    private void displayError(ErrorMessage errorMessage) {
        stateManager.out.println(Format.TEXT.set(Color.RED) + errorMessage.getError() + Format.resetAll());
    }

    private void loadGame(LoadGameMessage message) {
        currentGame = deserialize(message.getGame(), ChessGame.class);
        redrawBoard();
    }
}
