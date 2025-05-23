package server.handler;

import io.javalin.http.Context;
import model.response.EmptyResponse;
import model.response.result.ServiceException;
import service.AppService;

public class ClearHandler extends ObjectSerializer<EmptyResponse> {
    @Override
    public EmptyResponse serviceHandle(Context ignored) throws ServiceException {
        return AppService.clearData();
    }
}
