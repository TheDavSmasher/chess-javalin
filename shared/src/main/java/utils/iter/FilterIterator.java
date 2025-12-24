package utils.iter;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * Creates an {@link SelfIterable} that iterates over each item from the passed iterator
 * only if it satisfies the {@link Predicate} provided.
 * @param <T> The type of the iterator
 */
public class FilterIterator<T> implements SelfIterable<T> {
    private final Iterator<T> iterator;
    private final Predicate<T> predicate;
    private T fromHasNext = null;

    public FilterIterator(Iterator<T> iterator, Predicate<T> filter) {
        this.iterator = iterator;
        this.predicate = filter;
    }

    public FilterIterator(Iterable<T> iterable, Predicate<T> filter) {
        this(iterable.iterator(), filter);
    }

    @Override
    public boolean hasNext() {
        while (iterator.hasNext()) {
            T next = iterator.next();
            if (predicate.test(next)) {
                fromHasNext = next;
                return true;
            }
        }
        return false;
    }

    @Override
    public T next() {
        if (fromHasNext == null && !hasNext()) {
            throw new NoSuchElementException();
        }
        T next = fromHasNext;
        fromHasNext = null;
        return next;
    }
}
