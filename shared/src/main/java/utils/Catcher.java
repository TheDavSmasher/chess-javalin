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
        try {
            return supplier.get();
        } catch (Throwable e) {
            if (rethrowClass.isAssignableFrom(e.getClass())) {
                throw rethrowClass.cast(e);
            }
            if (catchClass.isInstance(e)) {
                try {
                    throw throwAsClass.getConstructor(String.class, Throwable.class).newInstance(errorMessage.apply(e), e);
                } catch (ReflectiveOperationException ignored) {}
                try {
                    throw throwAsClass.getConstructor(String.class).newInstance(errorMessage.apply(e));
                } catch (ReflectiveOperationException ignored) {}
            }
            throw new RuntimeException(e);
        }
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
}
