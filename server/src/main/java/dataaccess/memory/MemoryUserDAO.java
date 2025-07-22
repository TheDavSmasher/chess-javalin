package dataaccess.memory;

import model.dataaccess.UserData;
import dataaccess.DataAccessObject.*;

public class MemoryUserDAO extends MemoryDAO<UserData> implements UserDAO {
    @Override
    public UserData getUser(String username) {
        return get(UserData::username, username);
    }

    @Override
    public UserData getUser(String username, String password) {
        return get(user -> user.username().equals(username) && user.password().equals(password));
    }

    @Override
    public void createUser(String username, String password, String email) {
        add(new UserData(username, password, email));
    }
}
