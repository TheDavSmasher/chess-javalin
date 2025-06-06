package service;

import static service.Service.tryCatch;
import dataaccess.DataAccessObject.*;

public class AppService {
    public static void clearData() throws ServiceException {
        tryCatch(() -> {
            AuthDAO.getInstance().clear();
            UserDAO.getInstance().clear();
            GameDAO.getInstance().clear();
        });
    }
}
