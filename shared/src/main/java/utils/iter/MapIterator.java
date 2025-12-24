package utils.iter;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;

/**
 * Creates an {@link SelfIterable} that transforms each item from the passed iterator
 * to the new type, according to the mapper function
 * @param <T> The type of the inner iterator
 * @param <R> The type of the output iterator
 */
public class MapIterator<T, R> implements SelfIterable<R> {
    private final Iterator<T> iterator;
    private final Function<T, R> mapper;

    /**
     * Creates an {@link SelfIterable} that transforms each item from the passed {@param iterator}
     * to the new type, according to the {@param mapper} function
     * @param iterator The {@link Iterator} to transform.
     * @param mapper The transformer function.
     */
    public MapIterator(Iterator<T> iterator, Function<T, R> mapper) {
        this.iterator = iterator;
        this.mapper = mapper;
    }

    /**
     * Creates an {@link SelfIterable} that transforms each item from the passed {@param iterable}
     * to the new type, according to the {@param mapper} function
     * @param iterable The {@link Iterable} to transform.
     * @param mapper The transformer function.
     */
    public MapIterator(Iterable<T> iterable, Function<T, R> mapper) {
        this(iterable.iterator(), mapper);
    }

    /**
     * Creates an {@link SelfIterable} that transforms each item from the passed {@param items} array
     * to the new type, according to the {@param mapper} function
     * @param items The array to transform.
     * @param mapper The transformer function.
     */
    public MapIterator(T[] items, Function<T, R> mapper) {
        this(Arrays.asList(items), mapper);
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public R next() {
        return mapper.apply(iterator.next());
    }
}
