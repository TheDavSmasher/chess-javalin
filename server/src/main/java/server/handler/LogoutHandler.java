package server.handler;

public class LogoutHandler extends ObjectSerializer<EmptyResponse> {
    @Override
    public EmptyResponse serviceHandle(Context context) throws ServiceException {
        return UserService.logout(getAuthToken(context));
    }
}
