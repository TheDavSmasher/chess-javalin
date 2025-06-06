package chess.calculator;

import chess.ChessPosition;

public class CrossMoveCalculator extends LimitMoveCalculator {
    @Override
    protected int getSpace(ChessPosition start, boolean flipRow, boolean flipCol) {
        return mirrorIf(flipRow ? start.getRow() : start.getColumn(), flipCol);
    }

    @Override
    protected int getDirMod(boolean isRow, boolean flipRow, boolean flipCol, boolean ignored) {
        return getMod(isRow ^ flipRow, flipCol);
    }
}
