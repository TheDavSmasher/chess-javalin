package utils;

import java.util.function.Consumer;
import java.util.function.Function;

public final class Catcher {
    //region Throwing
    public static <T, E extends Exception> T throwNew(Class<E> exceptionClass, String message) throws E {
        ignoreReflection(() -> exceptionClass.getConstructor(String.class).newInstance(message));
        return null;
    }

    public static <T, E extends Exception> T throwNew(Class<E> exceptionClass) throws E {
        ignoreReflection(() -> exceptionClass.getConstructor().newInstance());
        return null;
    }
    //endregion

    //region Interfaces
    public interface ErrorSupplier<T> {
        T get() throws Throwable;
    }

    public interface ErrorRunnable {
        void run() throws Throwable;
    }

    public interface ErrorFunction<T, V> {
        V apply(T t) throws Throwable;
    }

    private interface ErrorConsumer<E extends Throwable> {
        void accept(Throwable t) throws E;
    }

    private interface ErrorSupplier0<T, E extends Throwable> {
        T get() throws E;
    }
    //endregion

    //region Rethrow
    public static <T, R extends Throwable> T tryCatchRethrow(
            ErrorSupplier<T> supplier, Class<? extends Throwable> catchClass, Class<R> rethrowClass
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
            ErrorRunnable runnable, Class<? extends Throwable> catchClass, Consumer<Throwable> postAction
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
            ErrorSupplier<A> supplier, ErrorFunction<A, T> function, Class<? extends Throwable> catchClass,
            Class<R> rethrowClass, Function<Throwable, String> errorMessage
    ) throws R {
        return tryCatchResourcesInner(supplier, r -> r, function, catchClass, rethrowClass, errorMessage);
    }
    //endregion

    //region Inner Methods
    private static <T, R extends Throwable> T tryCatchRethrowInner(
            ErrorSupplier<T> supplier, Class<? extends Throwable> catchClass, Class<R> rethrowClass,
            Class<? extends R> throwAsClass, Function<Throwable, String> errorMessage
    ) throws R {
        return tryCatchInner(supplier, catchClass, rethrowClass, e -> {
            ignoreReflection(() -> throwAsClass.getConstructor(String.class, Throwable.class).newInstance(errorMessage.apply(e), e));
            ignoreReflection(() -> throwAsClass.getConstructor(String.class).newInstance(errorMessage.apply(e)));
        });
    }

    private static <E extends Throwable> void ignoreReflection(ErrorSupplier0<E, ReflectiveOperationException> supplier0) throws E {
        try {
            throw supplier0.get();
        } catch (ReflectiveOperationException _) {}
    }

    private static <R extends Throwable> void tryCatchDoInner(
            ErrorRunnable runnable, Class<? extends Throwable> catchClass,
            Consumer<Throwable> postAction, Class<R> rethrowClass
    ) throws R {
        tryCatchInner(() -> { runnable.run(); return null; }, catchClass, rethrowClass, postAction::accept);
    }

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

    private static <T, R extends Throwable> T tryCatchInner(
            ErrorSupplier<T> supplier, Class<? extends Throwable> catchClass,
            Class<R> rethrowClass, ErrorConsumer<R> onCatch
    ) throws R {
        try {
            return supplier.get();
        } catch (Throwable e) {
            if (RuntimeException.class.isAssignableFrom(e.getClass())) {
                throw (RuntimeException) e;
            }
            if (rethrowClass != null && rethrowClass.isInstance(e)) {
                throw rethrowClass.cast(e);
            }
            if (catchClass != null && catchClass.isInstance(e)) {
                onCatch.accept(e);
            }
            throw new RuntimeException(e);
        }
    }
    //endregion
}
