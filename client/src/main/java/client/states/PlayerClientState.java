package client.states;

import backend.ServerFacade;
import chess.ChessMove;
import chess.ChessPiece;
import client.ChessClient;
import client.states.ClientCommandProcessing.*;

import java.io.IOException;
import java.io.PrintStream;
import java.util.function.Supplier;

import static utils.Catcher.tryCatchRethrow;

public class PlayerClientState extends InGameClientState {
    private final ClientCommand[] stateCommands = {
            redraw,
            new ClientCommand(this::makeMove, "Make Move", 2, 3,
                    """
                    Please provide a start and end position.
                    If a pawn is to be promoted, also provide what it should become.
                    ""","2 start end (pieceType)        Format positions column then row, such as G6.",
                    "select a piece in a given position and give its ending position.",
                    "Please make sure the move is legal."),
            highlight,
            leave,
            new ClientCommand(this::resignGame, "Resign",
                    "forfeit the current game, rendering it unplayable and making the opposing player as winner.",
                    "This action cannot be undone after you confirm.")
    };

    public PlayerClientState(
            ServerFacade serverFacade, PrintStream out, ClientChanger client,
            Supplier<String> authToken, Supplier<Integer> currentGameID, Supplier<Boolean> whitePlayer) {
        super(serverFacade, out, client, authToken, currentGameID, whitePlayer);
    }

    @Override
    protected ClientCommand[] getStateCommands() {
        return stateCommands;
    }

    private void makeMove(String[] params) throws IOException {
        String start = params[0], end = params[1];
        ChessPiece.PieceType type = params.length < 3 ? null : typeFromString(params[2]);
        ChessMove move = new ChessMove(positionFromString(start), positionFromString(end), type);
        serverFacade.makeMove(authToken.get(), currentGameID.get(), move);
    }

    private void resignGame() throws IOException {
        serverFacade.resignGame(authToken.get(), currentGameID.get());
        client.changeTo(ChessClient.MenuState.POST_LOGIN);
    }

    private ChessPiece.PieceType typeFromString(String type) throws IOException {
        return tryCatchRethrow(() -> ChessPiece.PieceType.valueOf(type),
                IllegalArgumentException.class, IOException.class, _ -> "That Piece Type does not exist.");
    }
}
