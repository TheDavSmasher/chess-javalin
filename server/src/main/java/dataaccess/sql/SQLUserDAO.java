package dataaccess.sql;

import dataaccess.DataAccessObject.*;
import dataaccess.DataAccessException;
import model.dataaccess.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.concurrent.atomic.AtomicBoolean;

public class SQLUserDAO extends SQLDAO implements UserDAO {
    public SQLUserDAO(AtomicBoolean tableCreated) throws DataAccessException {
        super(tableCreated);
    }

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
        return getUser(username) instanceof UserData userData && BCrypt.checkpw(password, userData.password())
                ? userData : null;
    }

    @Override
    public void createUser(String username, String password, String email) throws DataAccessException {
        tryInsert("username, password, email", SQLDAO::confirmUpdate,
                username, BCrypt.hashpw(password, BCrypt.gensalt()), email);
    }

    @Override
    protected String getTableSetup() {
        return """
                (
                  `username` varchar(255) NOT NULL,
                  `password` varchar(255) NOT NULL,
                  `email` varchar(255) NOT NULL,
                  PRIMARY KEY (`username`),
                  INDEX (`username`)
                )
                """;
    }
}
