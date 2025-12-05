package chess.calculator;

public class BishopMoveCalculator extends SymmetricCalculator{
    private static final IntTuple.Modifier[] modifiers = {
            IntTuple::invert, IntTuple::rotate
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
