package chess.calculator;

public class QueenMoveCalculator extends SymmetricCalculator {
    private static final IntTuple.Modifier[] modifiers = {
            IntTuple::invert, IntTuple::flatten, IntTuple::rotate
    };

    @Override
    protected IntTuple.Modifier[] getAxes() {
        return modifiers;
    }

    @Override
    protected boolean isContinuous() {
        return true;
    }
}
