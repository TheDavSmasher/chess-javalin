package server.handler;

import io.javalin.http.Context;
import service.ServiceException;
import service.AppService;

public class ClearHandler extends ResponseHandler<Void> {
    @Override
    public Void serviceHandle(Context ignored) throws ServiceException {
        return AppService.clearData();
    }
}
