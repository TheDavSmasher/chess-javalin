package chess.calculator;

import chess.*;

public abstract class SymmetricCalculator extends CombinationMoveCalculator {
    protected abstract IntTuple.Modifier[] getAxes();

    protected abstract boolean isContinuous();

    @Override
    protected int getCombinationCount() {
        return getAxes().length;
    }

    protected IntTuple.Modifier startModifier() {
        return IntTuple::self;
    }

    @Override
    protected IntTuple getEndOffset(IntTuple offset, ChessBoard board, ChessPosition start, Boolean... flips) {
        offset = startModifier().apply(offset);
        for (int i = 0; i < flips.length; i++) {
            if (flips[i]) {
                offset = getAxes()[i].apply(offset);
            }
        }
        return offset;
    }

    @Override
    protected Boolean endLoopCheck(ChessBoard board, ChessPosition end, Boolean... flips) {
        return isContinuous();
    }
}
