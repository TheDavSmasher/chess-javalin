package server.http;

import model.request.UserEnterRequest;
import model.response.UserEnterResponse;
import service.UserService;

public abstract class UserEnterHandler extends RequestHandler<UserEnterRequest, UserEnterResponse, UserService> {
    protected UserEnterHandler(UserService service) {
        super(service);
    }

    @Override
    protected Class<UserEnterRequest> getRequestClass() {
        return UserEnterRequest.class;
    }
}
