package utils.iter;

import org.jetbrains.annotations.NotNull;

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
}
