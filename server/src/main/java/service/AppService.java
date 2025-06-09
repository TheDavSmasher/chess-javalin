package service;

import dataaccess.DataAccessObject.*;

public class AppService extends Service {
    public static Void clearData() throws ServiceException {
        return tryCatch(() -> {
            AuthDAO.getInstance().clear();
            UserDAO.getInstance().clear();
            GameDAO.getInstance().clear();
            return null;
        });
    }
}
