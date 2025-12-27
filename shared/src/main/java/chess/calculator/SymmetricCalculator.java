package chess.calculator;

import chess.*;

import java.util.Collection;

public abstract class SymmetricCalculator extends CombinationMoveCalculator {
    protected abstract IntTuple.Modifier[] getAxes();

    protected abstract boolean isContinuous();

    @Override
    protected int getCombinationCount() {
        return getAxes().length;
    }

    @Override
    protected void addMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition start, ChessPosition end) {
        moves.add(new ChessMove(start, end));
    }

    protected IntTuple.Modifier startModifier() {
        return IntTuple::self;
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
