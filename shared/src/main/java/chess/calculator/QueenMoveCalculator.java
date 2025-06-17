package chess.calculator;

import chess.*;

import java.util.Collection;

public class QueenMoveCalculator implements PieceMoveCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition start) {
        Collection<ChessMove> endMoves = PieceMoveCalculatorFactory.getFrom(ChessPiece.PieceType.ROOK).calculateMoves(board, start);
        endMoves.addAll(PieceMoveCalculatorFactory.getFrom(ChessPiece.PieceType.BISHOP).calculateMoves(board, start));
        return endMoves;
    }
}
