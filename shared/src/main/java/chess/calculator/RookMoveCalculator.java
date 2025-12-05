package chess.calculator;

public class RookMoveCalculator extends SymmetricCalculator {
    private static final IntTuple.Modifier[] modifiers = {
            IntTuple::invert, IntTuple::rotate
    };

    @Override
    protected IntTuple.Modifier startModifier() {
        return IntTuple::flatten;
    }

    @Override
    protected IntTuple.Modifier[] getAxes() {
        return modifiers;
    }

    @Override
    protected boolean isContinuous() {
        return true;
    }
}
