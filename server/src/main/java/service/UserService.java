package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.dataaccess.AuthData;
import model.request.UserEnterRequest;
import model.response.EmptyResponse;
import model.response.UserEnterResponse;

import static org.eclipse.jetty.util.StringUtil.isBlank;
import static service.Service.tryCatch;

public class UserService {
    public static UserEnterResponse register(UserEnterRequest request) throws ServiceException {
        return tryCatch(() -> {
            if (isBlank(request.username()) || isBlank(request.password()) || isBlank(request.email())) {
                throw new BadRequestException();
            }

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
            if (isBlank(request.username()) || isBlank(request.password())) {
                throw new BadRequestException();
            }

            UserDAO userDAO = UserDAO.getInstance();
            AuthDAO authDAO = AuthDAO.getInstance();

            if (userDAO.getUser(request.username(), request.password()) == null) {
                throw new UnauthorizedException();
            }
            AuthData newAuth = authDAO.createAuth(request.username());
            return new UserEnterResponse(newAuth.username(), newAuth.authToken());
        });
    }

    public static EmptyResponse logout(String authToken) throws ServiceException {
        return tryCatch(() -> {
            validateAuth(authToken);
            AuthDAO authDAO = AuthDAO.getInstance();
            authDAO.deleteAuth(authToken);
            return new EmptyResponse();
        });
    }

    public static AuthData getUser(String authToken) throws ServiceException {
        return tryCatch(() -> {
            AuthDAO authDAO = AuthDAO.getInstance();
            return authDAO.getAuth(authToken);
        });
    }

    public static AuthData validateAuth(String authToken) throws ServiceException {
        AuthData userAuth = getUser(authToken);
        if (userAuth == null) {
            throw new UnauthorizedException();
        }
        return userAuth;
    }
}
