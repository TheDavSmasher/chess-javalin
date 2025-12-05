package chess.calculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public abstract class SymmetricCalculator implements PieceMoveCalculator {
    protected abstract int getAxes();

    protected abstract boolean isContinuous();

    protected abstract IntTuple getEndOffset(
            ChessBoard board, ChessPosition start, int offset, boolean... flips);

    protected boolean tryAdd(Collection<ChessMove> endMoves, ChessBoard board, ChessPosition start, ChessPosition end) {
        ChessPiece atEnd = board.getPiece(end);
        endMoves.add(new ChessMove(start, end));
        return atEnd == null;
    }

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition start) {
        Collection<ChessMove> moves = new ArrayList<>();
        int axes = getAxes();
        int perms = 1 << axes;

        boolean[] flips = new boolean[axes];

        for (int perm = 0; perm < perms; perm++) {
            for (int axis = 0, p = perm; axis < axes; axis++, p >>= 1) {
                flips[axis] = (p & 1) != 0;
            }

            boolean continuous = isContinuous();
            for (int i = 1; continuous || i <= 1; i++) {
                IntTuple endOffset = getEndOffset(board, start, i, flips);
                if (endOffset == null) {
                    break;
                }
                ChessPosition end = newPosition(start, endOffset);
                if (end.outOfBounds()) {
                    break;
                }
                ChessPiece atEnd = board.getPiece(end);
                if (atEnd != null && atEnd.color() == board.getPiece(start).color()) {
                    break;
                }
                // either is null or is opponent
                if (!tryAdd(moves, board, start, end)) {
                    break;
                }
            }
        }
        return moves;
    }

    protected static ChessPosition newPosition(ChessPosition start, IntTuple offset) {
        return new ChessPosition(start.getRow() + offset.a(), start.getColumn() + offset.b());
    }

    protected static int boolMod(boolean flip) {
        return flip ? -1 : 1;
    }
}
