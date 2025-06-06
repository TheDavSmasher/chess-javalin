package service;

import dataaccess.DataAccessException;
import model.dataaccess.AuthData;
import model.request.UserEnterRequest;
import model.response.UserEnterResponse;
import dataaccess.DataAccessObject.*;

import static org.eclipse.jetty.util.StringUtil.isBlank;
import static service.Service.*;

public class UserService {
    public static UserEnterResponse register(UserEnterRequest request) throws ServiceException {
        return enterUser(request, true, userDAO -> {
            if (userDAO.getUser(request.username()) != null) {
                throw new PreexistingException();
            }
            userDAO.createUser(request.username(), request.password(), request.email());
        });
    }

    public static UserEnterResponse login(UserEnterRequest request) throws ServiceException {
        return enterUser(request, false, userDAO -> {
            if (userDAO.getUser(request.username(), request.password()) == null) {
                throw new UnauthorizedException();
            }
        });
    }

    private interface EnterLogic {
        void enter(UserDAO userDAO) throws ServiceException, DataAccessException;
    }

    private static UserEnterResponse enterUser(UserEnterRequest request, boolean checkEmail, EnterLogic logic) throws ServiceException {
        return tryCatch(() -> {
            if (isBlank(request.username()) || isBlank(request.password()) || (checkEmail && isBlank(request.email()))) {
                throw new BadRequestException();
            }
            logic.enter(UserDAO.getInstance());
            AuthData authData = AuthDAO.getInstance().createAuth(request.username());
            return new UserEnterResponse(authData.username(), authData.authToken());
        });
    }

    public static void logout(String authToken) throws ServiceException {
        tryCatch(() -> {
            validateAuth(authToken);
            AuthDAO.getInstance().deleteAuth(authToken);
        });
    }

    public static String validateAuth(String authToken) throws ServiceException {
        return tryCatch(() -> AuthDAO.getInstance().getAuth(authToken) instanceof AuthData auth
                ? auth.username() : throwUnauthorized());
    }
}
