package server.handler;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import service.ServiceException;
import org.jetbrains.annotations.NotNull;

public abstract class ObjectSerializer<T> implements Handler {
    public void handle(@NotNull Context context) throws ServiceException {
        context.contentType("application/json").status(HttpStatus.OK).json(serviceHandle(context));
    }

    protected abstract T serviceHandle(Context context) throws ServiceException;

    protected static String getAuthToken(Context context) {
        return context.header("authorization");
    }
}
