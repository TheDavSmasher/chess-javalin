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

        for (var perm : new BooleanCombinations(1)) {
            boolean isRight = perm.iterator().next().value();
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
            int maxCol = (rookCol - start.getColumn()) * dirMod;
            for (int c = 1; c < maxCol; c++) {
                if (board.getPiece(new ChessPosition(teamRow, start.getColumn() + dirMod * c)) != null) {
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
