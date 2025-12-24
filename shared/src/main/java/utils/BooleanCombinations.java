package utils;

import org.jetbrains.annotations.NotNull;
import utils.iter.FilterIterator;
import utils.iter.SelfIterable;

import java.util.Iterator;

public class BooleanCombinations implements SelfIterable<BooleanCombinations.BoolCombination> {
    private final int count;
    private final int totalCombinations;
    private int currentIndex;

    public BooleanCombinations(int count) {
        this.count = count;
        this.totalCombinations = 1 << count;
        this.currentIndex = 0;
    }

    @Override
    public boolean hasNext() {
        return currentIndex < totalCombinations;
    }

    @Override
    public BoolCombination next() {
        BoolCombination nextCombination = new BoolCombination();
        currentIndex++;
        return nextCombination;
    }

    public record Combination(int index, boolean value){}

    public class BoolCombination implements Iterable<Combination> {
        @Override
        public @NotNull Iterator<Combination> iterator() {
            return new BoolCombinationIterator();
        }

        public Iterable<Combination> whereTrue() {
            return new FilterIterator<>(this, Combination::value);
        }

        private class BoolCombinationIterator implements Iterator<Combination> {
            private int index;
            private int value;

            public BoolCombinationIterator() {
                this.index = 0;
                this.value = currentIndex;
            }

            @Override
            public boolean hasNext() {
                return index < count;
            }

            @Override
            public Combination next() {
                Combination nextCombination = new Combination(index, (value & 1) != 0);
                index++;
                value >>= 1;
                return nextCombination;
            }
        }
    }
}
