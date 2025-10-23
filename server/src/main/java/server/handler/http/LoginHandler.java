package server.handler.http;

import model.request.UserEnterRequest;
import model.response.UserEnterResponse;
import service.ServiceException;
import service.UserService;

public class LoginHandler extends UserEnterHandler {
    @Override
    protected UserEnterResponse serviceCall(UserEnterRequest userEnterRequest, String ignored) throws ServiceException {
        return UserService.login(userEnterRequest);
    }
}
