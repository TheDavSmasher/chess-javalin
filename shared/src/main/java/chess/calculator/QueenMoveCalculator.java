package chess.calculator;

import chess.*;

public class QueenMoveCalculator extends SymmetricCalculator {
    @Override
    protected int getAxes() {
        return 3;
    }

    @Override
    protected boolean isContinuous() {
        return true;
    }

    @Override
    protected IntTuple getEndOffset(ChessBoard board, ChessPosition start, int offset, boolean... flips) {
        boolean invert = flips[0],
                flatten = flips[1],
                rotate = flips[2];
        IntTuple off = new IntTuple(offset);
        if (invert) {
            off = off.invert();
        }
        if (flatten) {
            off = off.flatten();
        }
        if (rotate) {
            off = off.rotate();
        }

        return off;
    }
}
