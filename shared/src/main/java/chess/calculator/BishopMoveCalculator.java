package chess.calculator;

import chess.ChessBoard;
import chess.ChessPosition;

public class BishopMoveCalculator extends SymmetricCalculator{
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
        boolean flipRow = flips[0],
                flipCol = flips[1];
        return new IntTuple(
                offset * boolMod(flipRow),
                offset * boolMod(flipCol));
    }
}
