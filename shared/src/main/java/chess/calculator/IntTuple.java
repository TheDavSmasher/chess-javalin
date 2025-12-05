package chess.calculator;

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

    public IntTuple self() {
        return this;
    }

    @FunctionalInterface
    public interface Modifier extends Function<IntTuple, IntTuple> {}
}
