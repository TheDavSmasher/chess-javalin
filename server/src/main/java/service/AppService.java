package service;

import dataaccess.DAOFactory;

public class AppService extends Service {
    public AppService(DAOFactory daoFactory) {
        super(daoFactory);
    }

    public Void clearData() throws ServiceException {
        return tryCatch(() -> {
            authDAO().clear();
            userDAO().clear();
            gameDAO().clear();
            return null;
        });
    }
}
