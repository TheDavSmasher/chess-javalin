package utils;

import java.util.function.Consumer;
import java.util.function.Function;

public final class Catcher {
    public interface ErrorSupplier<T> {
        T get() throws Throwable;
    }

    public static <T, R extends Throwable> T tryCatchRethrow(
            ErrorSupplier<T> supplier,
            Class<? extends Throwable> catchClass,
            Class<R> rethrowClass
    ) throws R {
        return tryCatchRethrowInner(supplier, catchClass, rethrowClass, rethrowClass, Throwable::getMessage);
    }

    public static <T, R extends Throwable> T tryCatchRethrow(
            ErrorSupplier<T> supplier,
            Class<? extends Throwable> catchClass,
            Class<R> rethrowClass,
            Class<? extends R> subclass
    ) throws R {
        return tryCatchRethrowInner(supplier, catchClass, rethrowClass, subclass, Throwable::getMessage);
    }

    public static <T, R extends Throwable> T tryCatchRethrow(
            ErrorSupplier<T> supplier,
            Class<? extends Throwable> catchClass,
            Class<R> rethrowClass,
            Function<Throwable, String> errorMessage
    ) throws R {
        return tryCatchRethrowInner(supplier, catchClass, rethrowClass, rethrowClass, errorMessage);
    }

    private static <T, R extends Throwable> T tryCatchRethrowInner(
            ErrorSupplier<T> supplier,
            Class<? extends Throwable> catchClass,
            Class<R> rethrowClass,
            Class<? extends R> throwAsClass,
            Function<Throwable, String> errorMessage
    ) throws R {
        return tryCatchInner(supplier, catchClass, rethrowClass, e -> {
            try {
                throw throwAsClass.getConstructor(String.class, Throwable.class).newInstance(errorMessage.apply(e), e);
            } catch (ReflectiveOperationException ignored) {}
            try {
                throw throwAsClass.getConstructor(String.class).newInstance(errorMessage.apply(e));
            } catch (ReflectiveOperationException ignored) {}
        });
    }

    public interface ErrorRunnable {
        void run() throws Throwable;
    }

    public static <R extends Throwable> void tryCatchDo(
            ErrorRunnable runnable,
            Class<? extends Throwable> catchClass,
            Consumer<Throwable> postAction,
            Class<R> rethrowClass
    ) throws R {
        tryCatchDoInner(runnable, catchClass, postAction, rethrowClass);
    }

    public static void tryCatchDo(
            ErrorRunnable runnable,
            Class<? extends Throwable> catchClass,
            Consumer<Throwable> postAction
    ) {
        tryCatchDoInner(runnable, catchClass, postAction, null);
    }

    public static void tryCatchIgnore(
            ErrorRunnable runnable,
            Class<? extends Throwable> catchClass
    ) {
        tryCatchDoInner(runnable, catchClass, e -> {}, null);
    }

    private static <R extends Throwable> void tryCatchDoInner(
            ErrorRunnable runnable,
            Class<? extends Throwable> catchClass,
            Consumer<Throwable> postAction,
            Class<R> rethrowClass
    ) throws R {
        tryCatchInner(() -> { runnable.run(); return null; }, catchClass, rethrowClass, postAction::accept);
    }

    public interface ErrorFunction<T, V> {
        V apply(T t) throws Throwable;
    }

    public static <T, M extends AutoCloseable, A extends AutoCloseable, R extends Throwable> T tryCatchResources(
            ErrorSupplier<M> supplier,
            ErrorFunction<M, A> subSupplier,
            ErrorFunction<A, T> function,
            Class<? extends Throwable> catchClass,
            Class<R> rethrowClass,
            Function<Throwable, String> errorMessage
    ) throws R {
        return tryCatchRethrow(() -> {
            try (M first = supplier.get(); A resource = subSupplier.apply(first)) {
                return function.apply(resource);
            }
        }, catchClass, rethrowClass, errorMessage);
    }

    public static <T, A extends AutoCloseable, R extends Throwable> T tryCatchResources(
            ErrorSupplier<A> supplier,
            ErrorFunction<A, T> function,
            Class<? extends Throwable> catchClass,
            Class<R> rethrowClass,
            Function<Throwable, String> errorMessage
    ) throws R {
        return tryCatchResources(supplier, r -> r, function, catchClass, rethrowClass, errorMessage);
    }

    private interface ErrorConsumer<E extends Throwable> {
        void accept(Throwable t) throws E;
    }

    private static <T, R extends Throwable> T tryCatchInner(
            ErrorSupplier<T> supplier,
            Class<? extends Throwable> catchClass,
            Class<R> rethrowClass,
            ErrorConsumer<R> onCatch
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
}
