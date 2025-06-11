package utils;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;
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

    public static <T, R extends Throwable> T catchRethrow(
            ErrorSupplier<T> supplier,
            Class<? extends Throwable> catchClass,
            Class<R> rethrowClass,
            Class<? extends R> subclass
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

    private static <T, R extends Throwable> T catchRethrow(
            ErrorSupplier<T> supplier,
            Class<? extends Throwable> catchClass,
            Class<R> rethrowClass,
            Class<? extends R> subclass,
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

    public interface ErrorRunnable {
        void run() throws Throwable;
    }

    public static <R extends Throwable> void catchAndDo(
            ErrorRunnable runnable,
            Class<? extends Throwable> catchClass,
            Consumer<Throwable> postAction,
            Class<R> rethrowClass
    ) throws R {
        try {
            runnable.run();
        } catch (Throwable e) {
            if (rethrowClass != null && rethrowClass.isInstance(e)) {
                throw rethrowClass.cast(e);
            }
            if (catchClass.isInstance(e)) {
                postAction.accept(e);
            }
        }
    }

    public static void catchAndDo(
            ErrorRunnable runnable,
            Class<? extends Throwable> catchClass,
            Consumer<Throwable> postAction
    ) {
        catchAndDo(runnable, catchClass, postAction, null);
    }
}
