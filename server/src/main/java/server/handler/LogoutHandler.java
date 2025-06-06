package server.handler;

import io.javalin.http.Context;
import service.ServiceException;
import service.UserService;

public class LogoutHandler extends ObjectSerializer<Void> {
    @Override
    public Void serviceHandle(Context context) throws ServiceException {
        UserService.logout(getAuthToken(context));
        return null;
    }
}
