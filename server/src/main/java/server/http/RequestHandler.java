package server.http;

import io.javalin.http.Context;
import service.Service;
import service.exception.ServiceException;

public abstract class RequestHandler<T, U, S extends Service>  extends ResponseHandler<U, S> {
    protected RequestHandler(S service) {
        super(service);
    }

    @Override
    protected U serviceHandle(Context context) throws ServiceException {
        return serviceCall(context.bodyAsClass(getRequestClass()), getAuthToken(context));
    }

    protected abstract U serviceCall(T serviceRequest, String authToken) throws ServiceException;

    protected abstract Class<T> getRequestClass();
}
