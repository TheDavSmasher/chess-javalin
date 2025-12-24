package chess.extracredit;

import chess.ChessMove;
import chess.ChessPosition;

public class CastlingMove extends ChessMove {
    public CastlingMove(ChessPosition startPosition, ChessPosition endPosition) {
        super(startPosition, endPosition);
    }
}
