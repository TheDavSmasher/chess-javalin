package service;

import static service.Service.tryCatch;
import dataaccess.DataAccessObject.*;

public class AppService {
    public static Void clearData() throws ServiceException {
        return tryCatch(() -> {
            AuthDAO.getInstance().clear();
            UserDAO.getInstance().clear();
            GameDAO.getInstance().clear();
            return null;
        });
    }
}
