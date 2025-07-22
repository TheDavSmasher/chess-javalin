package dataaccess.memory;

import dataaccess.DataAccessObject.*;

import java.util.HashSet;
import java.util.function.Function;
import java.util.function.Predicate;

public class MemoryDAO<T> implements ChessDAO {
    protected final HashSet<T> data = new HashSet<>();

    protected T get(Predicate<T> predicate) {
        return data.stream().filter(predicate).findFirst().orElse(null);
    }

    protected T get(Function<T, Object> factor, Object compared) {
        return get(value -> factor.apply(value).equals(compared));
    }

    protected T add(T value) {
        data.add(value);
        return value;
    }

    protected void remove(Function<T, Object> factor, Object compared) {
        data.removeIf(value -> factor.apply(value).equals(compared));
    }

    @Override
    public void clear() {
        data.clear();
    }
}
