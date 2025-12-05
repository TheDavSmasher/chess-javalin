package chess.calculator;

import chess.ChessPosition;

import java.util.function.Function;

public record IntTuple(int a, int b) {
    public IntTuple(int val) {
        this(val, val);
    }

    public IntTuple invert() {
        return new IntTuple(-a, -b);
    }

    public IntTuple rotate() {
        return new IntTuple(-b, a);
    }

    public IntTuple swap() {
        return new IntTuple(b, a);
    }

    public IntTuple flatten() {
        return new IntTuple(a, 0);
    }

    public IntTuple doubleA() {
        return new IntTuple(a * 2, b);
    }

    public IntTuple self() {
        return this;
    }

    @FunctionalInterface
    public interface Modifier extends Function<IntTuple, IntTuple> {}

    public ChessPosition newPosition(ChessPosition start) {
        return new ChessPosition(start.getRow() + a, start.getColumn() + b);
    }
}
