package chess.calculator;

import chess.ChessPiece;
import chess.PieceMoveCalculator;

public final class PieceMoveCalculatorFactory {
    public static PieceMoveCalculator getPieceMoveCalculator(ChessPiece.PieceType type) {
        return switch (type) {
            case BISHOP -> new DiagonalMoveCalculator();
            case ROOK -> new CrossMoveCalculator();
            case QUEEN -> new QueenMoveCalculator();
            case KING -> new KingMoveCalculator();
            case KNIGHT -> new KnightMoveCalculator();
            case PAWN -> new PawnMoveCalculator();
        };
    }
}
