package chess.calculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public abstract class SymmetricCalculator implements PieceMoveCalculator {
    protected IntTuple.Modifier startModifier() {
        return IntTuple::self;
    }

    protected abstract IntTuple.Modifier[] getAxes();

    protected abstract boolean isContinuous();

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition start) {
        Collection<ChessMove> moves = new ArrayList<>();

        IntTuple.Modifier[] axes = getAxes();
        int perms = 1 << axes.length;
        for (int perm = 0; perm < perms; perm++) {
            int i = 0;
            do {
                i++;
                IntTuple endOffset = startModifier().apply(new IntTuple(i, i));
                for (int axis = 0, p = perm; axis < axes.length; axis++, p >>= 1) {
                    if ((p & 1) != 0) {
                        endOffset = axes[axis].apply(endOffset);
                    }
                }
                ChessPosition end = endOffset.newPosition(start);
                if (end.outOfBounds()) {
                    break;
                }
                ChessPiece atEnd = board.getPiece(end);
                if (atEnd == null || atEnd.getTeamColor() != board.getPiece(start).getTeamColor()) {
                    moves.add(new ChessMove(start, end));
                }
                if (atEnd != null) {
                    break;
                }
            } while (isContinuous());
        }
        return moves;
    }
}
