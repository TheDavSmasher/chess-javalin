package server.handler;

import io.javalin.http.Context;
import model.response.result.ServiceException;

public abstract class RequestDeserializer<T, U>  extends ObjectSerializer<U> {
    @Override
    protected U serviceHandle(Context context) throws ServiceException {
        return serviceCall(context.bodyAsClass(getRequestClass()), getAuthToken(context));
    }

    protected abstract U serviceCall(T serviceRequest, String authToken) throws ServiceException;

    protected abstract Class<T> getRequestClass();
}
