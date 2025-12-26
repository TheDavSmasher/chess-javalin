package utils.iter;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Implementing this interface allows an object to be the target of the enhanced
 * {@code for} statement (sometimes called the "for-each loop" statement), while
 * providing the iteration logic and state itself.
 *
 * @param <T> the type of elements returned by the iterator
 */
public interface SelfIterable<T> extends Iterable<T>, Iterator<T> {
    default @NotNull Iterator<T> iterator() {
        return this;
    }

    static <T> Collection<T> asCollection(Iterable<T> iterable) {
        Collection<T> collection = new ArrayList<>();
        for (T element : iterable)
            collection.add(element);
        return collection;
    }

    @SuppressWarnings("unchecked")
    static <T> T[] asArray(Iterable<T> iterable) {
        Collection<T> collection = asCollection(iterable);
        T[] array = (T[]) Array.newInstance(collection.iterator().next().getClass(), collection.size());
        int i = 0;
        for (T element : collection)
            array[i++] = element;
        return array;
    }
}
