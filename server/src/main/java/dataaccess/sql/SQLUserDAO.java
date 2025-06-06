package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.dataaccess.UserData;
import org.mindrot.jbcrypt.BCrypt;

public class SQLUserDAO extends SQLDAO implements UserDAO {
    private static SQLUserDAO instance;

    public SQLUserDAO () throws DataAccessException {}

    @Override
    protected String getTableName() {
        return "users";
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return trySingleQuery("username", username, rs -> new UserData(
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("email"))
        );
    }

    @Override
    public UserData getUser(String username, String password) throws DataAccessException {
        UserData userData = getUser(username);
        if (userData == null || !BCrypt.checkpw(password, userData.password())) return null;
        return userData;
    }

    @Override
    public void createUser(String username, String password, String email) throws DataAccessException {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        tryUpdate("INSERT INTO users (username, password, email) VALUES (?, ?, ?)",
                SQLDAO::confirmUpdate, username, hashedPassword, email);
    }

    public static UserDAO getInstance() throws DataAccessException {
        return instance == null ? (instance = new SQLUserDAO()) : instance;
    }
}
