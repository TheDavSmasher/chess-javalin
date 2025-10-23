package server.handler.http;

import model.request.UserEnterRequest;
import model.response.UserEnterResponse;

public abstract class UserEnterHandler extends RequestHandler<UserEnterRequest, UserEnterResponse> {
    @Override
    protected Class<UserEnterRequest> getRequestClass() {
        return UserEnterRequest.class;
    }
}
