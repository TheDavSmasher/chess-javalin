package chess.calculator;

import chess.ChessBoard;
import chess.ChessPosition;

public class RookMoveCalculator extends SymmetricCalculator {
    @Override
    protected int getAxes() {
        return 2;
    }

    @Override
    protected boolean isContinuous() {
        return true;
    }

    @Override
    protected IntTuple getEndOffset(ChessBoard board, ChessPosition start, int offset, boolean... flips) {
        boolean invert = flips[0],
                swap = flips[1];
        IntTuple off = new IntTuple(offset * boolMod(invert), 0);
        if (swap) {
            off = off.swap();
        }
        return off;
    }
}
