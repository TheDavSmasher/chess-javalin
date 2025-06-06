package server.handler;

import io.javalin.http.Context;
import service.ServiceException;
import service.AppService;

public class ClearHandler extends ObjectSerializer<Void> {
    @Override
    public Void serviceHandle(Context ignored) throws ServiceException {
        AppService.clearData();
        return null;
    }
}
