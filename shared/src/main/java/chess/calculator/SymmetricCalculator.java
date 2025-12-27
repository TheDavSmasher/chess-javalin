package chess.calculator;

import chess.*;

import java.util.Collection;

public abstract class SymmetricCalculator extends CombinationMoveCalculator {
    protected IntTuple.Modifier startModifier() {
        return IntTuple::self;
    }

    protected abstract IntTuple.Modifier[] getAxes();

    @Override
    protected int getCombinationCount() {
        return getAxes().length;
    }

    protected abstract boolean isContinuous();

    @Override
    protected void addMoves(Collection<ChessMove> moves, ChessPosition start, ChessPosition end) {
        moves.add(new ChessMove(start, end));
    }

    @Override
    protected IntTuple getEndOffset(ChessBoard board, ChessPosition start, Boolean... flips) {
        IntTuple endOffset = startModifier().apply(new IntTuple(1));
        for (int i = 0; i < flips.length; i++) {
            if (flips[i]) {
                endOffset = getAxes()[i].apply(endOffset);
            }
        }
        return endOffset;
    }

    @Override
    protected Boolean endLoopCheck(ChessPiece atEnd, Boolean... flips) {
        return isContinuous();
    }
}
