package chess.calculator;

import chess.*;
import utils.BooleanCombinations;

import java.util.Collection;

public class KingMoveCalculator extends SymmetricCalculator {
    private static final IntTuple.Modifier[] modifiers = {
            IntTuple::invert, IntTuple::flatten, IntTuple::rotate
    };

    @Override
    protected IntTuple.Modifier[] getAxes() {
        return modifiers;
    }

    @Override
    protected boolean isContinuous() {
        return false;
    }

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition start) {
        Collection<ChessMove> moves = super.calculateMoves(board, start);

        ChessPiece king = board.getPiece(start);
        int teamRow = king.getTeamColor().initialRow();
        if (king.hasMoved() || start.getRow() != teamRow) {
            return moves;
        }

        for (var isRight : new BooleanCombinations(1).next().values()) {
            int dirMod = isRight ? 1 : -1;
            ChessPosition endPosition = new ChessPosition(teamRow, start.getColumn() + dirMod * 2);
            if (endPosition.outOfBounds()) {
                continue;
            }

            int rookCol = isRight ? 8 : 1;
            ChessPosition rookPos = new ChessPosition(teamRow, rookCol);
            if (!(board.getPiece(rookPos) instanceof ChessPiece rook) || rook.hasMoved()) {
                continue;
            }

            boolean cleanPath = true;
            for (int c = start.getColumn() + dirMod; c < rookCol; c += dirMod) {
                if (board.getPiece(new ChessPosition(teamRow, c)) != null) {
                    cleanPath = false;
                    break;
                }
            }
            if (cleanPath) {
                moves.add(new ChessMove(start, endPosition));
            }
        }

        return moves;
    }
}
