package service;

import dataaccess.DataAccessException;
import model.dataaccess.AuthData;
import model.dataaccess.UserData;
import model.request.UserEnterRequest;
import model.response.UserEnterResponse;
import org.mindrot.jbcrypt.BCrypt;

import static utils.Catcher.*;

public class UserService extends Service {
    public static UserEnterResponse register(UserEnterRequest request) throws ServiceException {
        return enterUser(request, true, () -> {
            if (userDAO().getUser(request.username()) != null) {
                throwNew(PreexistingException.class);
            }
            userDAO().createUser(
                    request.username(), BCrypt.hashpw(request.password(), BCrypt.gensalt()), request.email());
        });
    }

    public static UserEnterResponse login(UserEnterRequest request) throws ServiceException {
        return enterUser(request, false, () -> {
            if (!(userDAO().getUser(request.username()) instanceof UserData data) ||
                    !BCrypt.checkpw(request.password(), data.password())) {
                throwNew(UnauthorizedException.class);
            }
        });
    }

    public static Void logout(String authToken) throws ServiceException {
        return tryAuthorized(authToken, _ -> authDAO().deleteAuth(authToken));
    }

    public static String validateAuth(String authToken) throws ServiceException {
        return tryCatch(() -> authDAO().getAuth(authToken) instanceof AuthData auth
                ? auth.username() : throwNew(UnauthorizedException.class));
    }

    @FunctionalInterface
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
}
