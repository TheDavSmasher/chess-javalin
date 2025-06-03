package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.dataaccess.AuthData;
import model.request.UserEnterRequest;
import model.response.UserEnterResponse;

import static org.eclipse.jetty.util.StringUtil.isBlank;
import static service.Service.tryCatch;

public class UserService {
    public static UserEnterResponse register(UserEnterRequest request) throws ServiceException {
        return tryCatch(() -> {
            throwIfInsufficient(request, true);

            UserDAO userDAO = UserDAO.getInstance();
            if (userDAO.getUser(request.username()) != null) {
                throw new PreexistingException();
            }
            userDAO.createUser(request.username(), request.password(), request.email());
            return login(request);
        });
    }

    public static UserEnterResponse login(UserEnterRequest request) throws ServiceException {
        return tryCatch(() -> {
            throwIfInsufficient(request, false);

            if (UserDAO.getInstance().getUser(request.username(), request.password()) == null) {
                throw new UnauthorizedException();
            }
            AuthData newAuth = AuthDAO.getInstance().createAuth(request.username());
            return new UserEnterResponse(newAuth.username(), newAuth.authToken());
        });
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

    public static AuthData validateAuth(String authToken) throws ServiceException {
        return tryCatch(() -> {
            AuthData userAuth = AuthDAO.getInstance().getAuth(authToken);
            if (userAuth == null) {
                throw new UnauthorizedException();
            }
            return userAuth;
        });
    }
}
