package dataaccess.memory;

import dataaccess.DataAccessObject.*;

import java.util.HashSet;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class MemoryDAO<T> implements ChessDAO {
    protected final HashSet<T> data = new HashSet<>();

    protected T get(Predicate<T> predicate) {
        return data.stream().filter(predicate).findFirst().orElse(null);
    }

    protected <O> T get(Function<T, O> factor, O compared) {
        return get(value -> factor.apply(value).equals(compared));
    }

    protected T add(T value) {
        data.add(value);
        return value;
    }

    protected <O> void remove(Function<T, O> factor, O compared) {
        data.removeIf(value -> factor.apply(value).equals(compared));
    }

    @Override
    public void clear() {
        data.clear();
    }
}
