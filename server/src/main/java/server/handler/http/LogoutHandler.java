package server.handler.http;

import io.javalin.http.Context;
import service.ServiceException;
import service.UserService;

public class LogoutHandler extends ResponseHandler<Void> {
    @Override
    public Void serviceHandle(Context context) throws ServiceException {
        return UserService.logout(getAuthToken(context));
    }
}
