package server.handler.http;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import service.Service;
import service.ServiceException;
import org.jetbrains.annotations.NotNull;

public abstract class ResponseHandler<T, S extends Service> implements Handler {
    protected final S service;

    protected ResponseHandler(S service) {
        this.service = service;
    }

    public void handle(@NotNull Context context) throws ServiceException {
        context.contentType("application/json").status(HttpStatus.OK).json(serviceHandle(context) instanceof T res ? res : "");
    }

    protected abstract T serviceHandle(Context context) throws ServiceException;

    protected String getAuthToken(Context context) {
        return context.header("authorization");
    }
}
