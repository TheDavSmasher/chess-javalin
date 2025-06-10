package server.handler;

import io.javalin.http.Context;
import service.ServiceException;

public abstract class RequestHandler<T, U>  extends ResponseHandler<U> {
    @Override
    protected U serviceHandle(Context context) throws ServiceException {
        return serviceCall(context.bodyAsClass(getRequestClass()), getAuthToken(context));
    }

    protected abstract U serviceCall(T serviceRequest, String authToken) throws ServiceException;

    protected abstract Class<T> getRequestClass();
}
