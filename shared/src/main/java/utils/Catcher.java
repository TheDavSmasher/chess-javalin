package utils;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

public final class Catcher {
    public interface ErrorSupplier<T> {
        T get() throws Throwable;
    }

    public static <T, R extends Throwable> T catchRethrow(
            ErrorSupplier<T> supplier,
            Class<? extends Throwable> catchClass,
            Class<R> rethrowClass
    ) throws R {
        return catchRethrow(supplier, catchClass, rethrowClass, rethrowClass, Throwable::getMessage);
    }

    public static <T, R extends Throwable, S extends R> T catchRethrow(
            ErrorSupplier<T> supplier,
            Class<? extends Throwable> catchClass,
            Class<R> rethrowClass,
            Class<S> subclass
    ) throws R {
        return catchRethrow(supplier, catchClass, rethrowClass, subclass, Throwable::getMessage);
    }

    public static <T, R extends Throwable> T catchRethrow(
            ErrorSupplier<T> supplier,
            Class<? extends Throwable> catchClass,
            Class<R> rethrowClass,
            Function<Throwable, String> errorMessage
    ) throws R {
        return catchRethrow(supplier, catchClass, rethrowClass, rethrowClass, errorMessage);
    }

    public static <T, R extends Throwable, S extends R> T catchRethrow(
            ErrorSupplier<T> supplier,
            Class<? extends Throwable> catchClass,
            Class<R> rethrowClass,
            Class<S> subclass,
            Function<Throwable, String> errorMessage
    ) throws R {
        try {
            return supplier.get();
        } catch (Throwable e) {
            if (rethrowClass.isAssignableFrom(e.getClass())) {
                throw rethrowClass.cast(e);
            }
            if (catchClass.isInstance(e)) {
                try {
                    throw subclass.getConstructor(String.class).newInstance(errorMessage.apply(e));
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                         InvocationTargetException ignored) {}
            }
            throw new RuntimeException(e);
        }
    }
}
