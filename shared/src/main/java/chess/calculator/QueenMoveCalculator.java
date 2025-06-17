package chess.calculator;

import chess.*;

import java.util.Collection;

public class QueenMoveCalculator implements PieceMoveCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition start) {
        Collection<ChessMove> endMoves = PieceMoveCalculatorFactory.INSTANCE.get(ChessPiece.PieceType.ROOK).calculateMoves(board, start);
        endMoves.addAll(PieceMoveCalculatorFactory.INSTANCE.get(ChessPiece.PieceType.BISHOP).calculateMoves(board, start));
        return endMoves;
    }
}
