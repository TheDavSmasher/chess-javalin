package model;

import java.lang.reflect.InvocationTargetException;

public final class Catcher {
    public interface ErrorSupplier<T> {
        T get() throws Exception;
    }

    public static <T, R extends Exception> T catchRethrow(ErrorSupplier<T> supplier, Class<? extends Exception> catchClass, Class<R> rethrowClass) throws R {
        try {
            return supplier.get();
        } catch (Exception e) {
            if (rethrowClass.isInstance(e)) {
                throw rethrowClass.cast(e);
            }
            if (catchClass.isInstance(e)) {
                try {
                    throw rethrowClass.getConstructor(String.class).newInstance(e.getMessage());
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                         InvocationTargetException ignored) {}
            }
            throw new RuntimeException(e);
        }
    }
}
