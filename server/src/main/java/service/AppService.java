package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.response.EmptyResponse;

import static service.Service.tryCatch;

public class AppService {
    public static EmptyResponse clearData() throws ServiceException {
        return tryCatch(() -> {
            AuthDAO.getInstance().clear();
            UserDAO.getInstance().clear();
            GameDAO.getInstance().clear();
            return new EmptyResponse();
        });
    }
}
