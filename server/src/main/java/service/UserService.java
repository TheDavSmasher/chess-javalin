package service;

import dataaccess.DataAccessException;
import model.dataaccess.AuthData;
import model.request.UserEnterRequest;
import model.response.UserEnterResponse;
import dataaccess.DataAccessObject.*;

import static org.eclipse.jetty.util.StringUtil.isBlank;

public class UserService extends Service {
    public static UserEnterResponse register(UserEnterRequest request) throws ServiceException {
        return enterUser(request, true, () -> {
            if (UserDAO.getInstance().getUser(request.username()) != null) {
                throw new PreexistingException();
            }
            UserDAO.getInstance().createUser(request.username(), request.password(), request.email());
        });
    }

    public static UserEnterResponse login(UserEnterRequest request) throws ServiceException {
        return enterUser(request, false, () -> {
            if (UserDAO.getInstance().getUser(request.username(), request.password()) == null) {
                throw new UnauthorizedException();
            }
        });
    }

    private interface EndpointRunnable {
        void method() throws ServiceException, DataAccessException;
    }

    private static UserEnterResponse enterUser(UserEnterRequest request, boolean checkEmail, EndpointRunnable logic) throws ServiceException {
        return tryCatch(() -> {
            if (isBlank(request.username()) || isBlank(request.password()) || (checkEmail && isBlank(request.email()))) {
                throw new BadRequestException();
            }
            logic.method();
            AuthData authData = AuthDAO.getInstance().createAuth(request.username());
            return new UserEnterResponse(authData.username(), authData.authToken());
        });
    }

    public static Void logout(String authToken) throws ServiceException {
        return tryAuthorized(authToken, ignored -> AuthDAO.getInstance().deleteAuth(authToken));
    }

    public static String validateAuth(String authToken) throws ServiceException {
        return tryCatch(() -> AuthDAO.getInstance().getAuth(authToken) instanceof AuthData auth
                ? auth.username() : throwUnauthorized());
    }

    //region Interfaces
    public interface AuthorizedSupplier<T> {
        T call() throws ServiceException, DataAccessException;
    }

    public interface AuthorizedConsumer {
        void call(String username) throws ServiceException, DataAccessException;
    }
    //endregion

    public static <T> T tryAuthorized(String authToken, AuthorizedSupplier<T> logic) throws ServiceException {
        return tryCatch(() -> {
            validateAuth(authToken);
            return logic.call();
        });
    }

    public static <T> T tryAuthorized(String authToken, AuthorizedConsumer logic) throws ServiceException {
        return tryCatch(() -> {
            logic.call(validateAuth(authToken));
            return null;
        });
    }
}
