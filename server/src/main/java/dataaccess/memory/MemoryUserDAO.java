package dataaccess.memory;

import model.dataaccess.UserData;
import dataaccess.DataAccessObject.*;

public class MemoryUserDAO extends MemoryDAO<UserData> implements UserDAO {
    @Override
    public UserData getUser(String username) {
        for (UserData user : data) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        return null;
    }


    @Override
    public UserData getUser(String username, String password) {
        for (UserData user : data) {
            if (user.username().equals(username) && user.password().equals(password)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public void createUser(String username, String password, String email) {
        data.add(new UserData(username, password, email));
    }
}
