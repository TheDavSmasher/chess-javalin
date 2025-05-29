package server.handler;

import io.javalin.http.Context;
import model.response.EmptyResponse;
import service.ServiceException;
import service.UserService;

public class LogoutHandler extends ObjectSerializer<EmptyResponse> {
    @Override
    public EmptyResponse serviceHandle(Context context) throws ServiceException {
        return UserService.logout(getAuthToken(context));
    }
}
