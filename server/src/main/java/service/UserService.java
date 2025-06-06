package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.dataaccess.AuthData;
import model.request.UserEnterRequest;
import model.response.UserEnterResponse;

import static org.eclipse.jetty.util.StringUtil.isBlank;
import static service.Service.*;

public class UserService {
    public static UserEnterResponse register(UserEnterRequest request) throws ServiceException {
        return tryCatch(() -> {
            throwIfInsufficient(request, true);

            UserDAO userDAO = UserDAO.getInstance();
            if (userDAO.getUser(request.username()) != null) {
                throw new PreexistingException();
            }
            userDAO.createUser(request.username(), request.password(), request.email());
            return enterUser(request);
        });
    }

    public static UserEnterResponse login(UserEnterRequest request) throws ServiceException {
        return tryCatch(() -> {
            throwIfInsufficient(request, false);
            return UserDAO.getInstance().getUser(request.username(), request.password()) == null
                    ? throwUnauthorized() : enterUser(request);
        });
    }

    private static UserEnterResponse enterUser(UserEnterRequest request) throws DataAccessException {
        AuthData authData = AuthDAO.getInstance().createAuth(request.username());
        return new UserEnterResponse(authData.username(), authData.authToken());
    }

    private static void throwIfInsufficient(UserEnterRequest request, boolean checkEmail) throws ServiceException {
        if (isBlank(request.username()) || isBlank(request.password()) || (checkEmail && isBlank(request.email()))) {
            throw new BadRequestException();
        }
    }

    public static Void logout(String authToken) throws ServiceException {
        return tryCatch(() -> {
            validateAuth(authToken);
            AuthDAO.getInstance().deleteAuth(authToken);
            return null;
        });
    }

    public static String validateAuth(String authToken) throws ServiceException {
        return tryCatch(() -> AuthDAO.getInstance().getAuth(authToken) instanceof AuthData auth
                ? auth.username() : throwUnauthorized());
    }
}
