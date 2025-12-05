package chess.calculator;

public class KnightMoveCalculator extends SymmetricCalculator {
    private static final IntTuple.Modifier[] modifiers = {
            IntTuple::invert, IntTuple::rotate, IntTuple::swap
    };

    @Override
    protected IntTuple.Modifier startModifier() {
        return IntTuple::doubleA;
    }

    @Override
    protected IntTuple.Modifier[] getAxes() {
        return modifiers;
    }

    @Override
    protected boolean isContinuous() {
        return false;
    }
}
