package server.http;

import io.javalin.http.Context;
import service.exception.ServiceException;
import service.UserService;

public class LogoutHandler extends ResponseHandler<Void, UserService> {
    public LogoutHandler(UserService service) {
        super(service);
    }

    @Override
    public Void serviceHandle(Context context) throws ServiceException {
        return service.logout(getAuthToken(context));
    }
}
