package utils;

import java.util.function.Consumer;
import java.util.function.Function;

public final class Catcher {
    //region Throwing
    private static final Class<?>[] FULL_ERROR = {String.class, Throwable.class};
    private static final Class<?>[] MSG_ERROR = {String.class};

    public static <T, E extends Exception> T throwNew(Class<E> exceptionClass, String message) throws E {
        createError(exceptionClass, MSG_ERROR, message);
        return null;
    }

    public static <T, E extends Exception> T throwNew(Class<E> exceptionClass) throws E {
        createError(exceptionClass, new Class[0]);
        return null;
    }
    //endregion

    //region Interfaces
    @FunctionalInterface
    public interface ErrorSupplier<T> {
        T get() throws Throwable;
    }

    @FunctionalInterface
    public interface ErrorRunnable {
        void run() throws Throwable;
    }

    @FunctionalInterface
    public interface ErrorFunction<T, V> {
        V apply(T t) throws Throwable;
    }

    @FunctionalInterface
    private interface ErrorConsumer<E extends Throwable> {
        void accept(Throwable t) throws E;
    }
    //endregion

    //region Rethrow
    public static <T, R extends Throwable> T tryCatchRethrow(
            ErrorSupplier<T> supplier, Class<? extends Throwable> catchClass,
            Class<R> rethrowClass
    ) throws R {
        return tryCatchRethrowInner(supplier, catchClass, rethrowClass, rethrowClass, Throwable::getMessage);
    }

    public static <T, R extends Throwable> T tryCatchRethrow(
            ErrorSupplier<T> supplier, Class<? extends Throwable> catchClass,
            Class<R> rethrowClass, Class<? extends R> subclass
    ) throws R {
        return tryCatchRethrowInner(supplier, catchClass, rethrowClass, subclass, Throwable::getMessage);
    }

    public static <T, R extends Throwable> T tryCatchRethrow(
            ErrorSupplier<T> supplier, Class<? extends Throwable> catchClass,
            Class<R> rethrowClass, Function<Throwable, String> errorMessage
    ) throws R {
        return tryCatchRethrowInner(supplier, catchClass, rethrowClass, rethrowClass, errorMessage);
    }
    //endregion

    //region Do
    public static <R extends Throwable> void tryCatchDo(
            ErrorRunnable runnable, Class<? extends Throwable> catchClass,
            Consumer<Throwable> postAction, Class<R> rethrowClass
    ) throws R {
        tryCatchDoInner(runnable, catchClass, postAction, rethrowClass);
    }

    public static void tryCatchDo(
            ErrorRunnable runnable, Class<? extends Throwable> catchClass,
            Consumer<Throwable> postAction
    ) {
        tryCatchDoInner(runnable, catchClass, postAction, null);
    }
    //endregion

    //region Resources
    public static <T, M extends AutoCloseable, A extends AutoCloseable, R extends Throwable> T tryCatchResources(
            ErrorSupplier<M> supplier, ErrorFunction<M, A> subSupplier, ErrorFunction<A, T> function,
            Class<? extends Throwable> catchClass, Class<R> rethrowClass, Function<Throwable, String> errorMessage
    ) throws R {
        return tryCatchResourcesInner(supplier, subSupplier, function, catchClass, rethrowClass, errorMessage);
    }

    public static <T, A extends AutoCloseable, R extends Throwable> T tryCatchResources(
            ErrorSupplier<A> supplier, ErrorFunction<A, T> function,
            Class<? extends Throwable> catchClass, Class<R> rethrowClass, Function<Throwable, String> errorMessage
    ) throws R {
        return tryCatchResourcesInner(supplier, r -> r, function, catchClass, rethrowClass, errorMessage);
    }
    //endregion

    //region Inner Methods
    private static <T, M extends AutoCloseable, A extends AutoCloseable, R extends Throwable> T tryCatchResourcesInner(
            ErrorSupplier<M> supplier, ErrorFunction<M, A> subSupplier, ErrorFunction<A, T> function,
            Class<? extends Throwable> catchClass, Class<R> rethrowClass, Function<Throwable, String> errorMessage
    ) throws R {
        return tryCatchRethrowInner(() -> {
            try (M first = supplier.get(); A resource = subSupplier.apply(first)) {
                return function.apply(resource);
            }
        }, catchClass, rethrowClass, rethrowClass, errorMessage);
    }

    private static <T, R extends Throwable> T tryCatchRethrowInner(
            ErrorSupplier<T> supplier, Class<? extends Throwable> catchClass, Class<R> rethrowClass,
            Class<? extends R> throwAsClass, Function<Throwable, String> errorMessage
    ) throws R {
        return tryCatchInner(supplier, catchClass, rethrowClass, e -> {
            createError(throwAsClass, FULL_ERROR, errorMessage.apply(e), e);
            createError(throwAsClass, MSG_ERROR, errorMessage.apply(e));
        });
    }

    private static <E extends Throwable> void createError(Class<E> throwAs, Class<?>[] types, Object... values) throws E {
        try {
            throw throwAs.getConstructor(types).newInstance(values);
        } catch (ReflectiveOperationException _) {}
    }

    private static <R extends Throwable> void tryCatchDoInner(
            ErrorRunnable runnable, Class<? extends Throwable> catchClass,
            Consumer<Throwable> postAction, Class<R> rethrowClass
    ) throws R {
        tryCatchInner(() -> { runnable.run(); return null; }, catchClass, rethrowClass, postAction::accept);
    }

    private static <T, R extends Throwable> T tryCatchInner(
            ErrorSupplier<T> supplier, Class<? extends Throwable> catchClass,
            Class<R> rethrowClass, ErrorConsumer<R> onCatch
    ) throws R {
        try {
            return supplier.get();
        } catch (Throwable e) {
            if (rethrowClass != null && rethrowClass.isInstance(e)) {
                throw rethrowClass.cast(e);
            }
            if (catchClass != null && catchClass.isInstance(e)) {
                onCatch.accept(e);
            }
            if (RuntimeException.class.isAssignableFrom(e.getClass())) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }
    //endregion
}
