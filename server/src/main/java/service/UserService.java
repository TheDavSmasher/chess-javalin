package service;

import dataaccess.DataAccessException;
import model.dataaccess.AuthData;
import model.request.UserEnterRequest;
import model.response.UserEnterResponse;

public class UserService extends Service {
    public static UserEnterResponse register(UserEnterRequest request) throws ServiceException {
        return enterUser(request, true, () -> {
            if (userDAO().getUser(request.username()) != null) {
                throwPreexisting();
            }
            userDAO().createUser(request.username(), request.password(), request.email());
        });
    }

    public static UserEnterResponse login(UserEnterRequest request) throws ServiceException {
        return enterUser(request, false, () -> {
            if (userDAO().getUser(request.username(), request.password()) == null) {
                throwUnauthorized();
            }
        });
    }

    private interface EndpointRunnable {
        void method() throws ServiceException, DataAccessException;
    }

    private static UserEnterResponse enterUser(UserEnterRequest request, boolean checkEmail, EndpointRunnable logic) throws ServiceException {
        return tryCatch(() -> {
            String username = getValidParameters(request.username(), request.password(), checkEmail ? request.email() : ".");
            logic.method();
            return new UserEnterResponse(username, authDAO().createAuth(username).authToken());
        });
    }

    public static Void logout(String authToken) throws ServiceException {
        return tryAuthorized(authToken, ignored -> authDAO().deleteAuth(authToken));
    }

    public static String validateAuth(String authToken) throws ServiceException {
        return tryCatch(() -> authDAO().getAuth(authToken) instanceof AuthData auth
                ? auth.username() : throwUnauthorized());
    }
}
