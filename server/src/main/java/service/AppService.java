package service;

public class AppService extends Service {
    public static Void clearData() throws ServiceException {
        return tryCatch(() -> {
            authDAO().clear();
            userDAO().clear();
            gameDAO().clear();
            return null;
        });
    }
}
