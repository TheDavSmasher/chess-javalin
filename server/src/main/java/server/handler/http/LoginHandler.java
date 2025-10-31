package server.handler.http;

import model.request.UserEnterRequest;
import model.response.UserEnterResponse;
import service.ServiceException;
import service.UserService;

public class LoginHandler extends UserEnterHandler {
    public LoginHandler(UserService service) {
        super(service);
    }

    @Override
    protected UserEnterResponse serviceCall(UserEnterRequest userEnterRequest, String ignored) throws ServiceException {
        return service.login(userEnterRequest);
    }
}
