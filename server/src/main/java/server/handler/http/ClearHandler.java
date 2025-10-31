package server.handler.http;

import io.javalin.http.Context;
import service.ServiceException;
import service.AppService;

public class ClearHandler extends ResponseHandler<Void, AppService> {
    public ClearHandler(AppService service) {
        super(service);
    }

    @Override
    public Void serviceHandle(Context ignored) throws ServiceException {
        return service.clearData();
    }
}
