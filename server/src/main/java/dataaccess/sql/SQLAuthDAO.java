package dataaccess.sql;

import dataaccess.DataAccessObject.*;
import dataaccess.DataAccessException;
import model.dataaccess.AuthData;

import java.util.UUID;

public class SQLAuthDAO extends SQLDAO implements AuthDAO {
    private static SQLAuthDAO instance;
    private static boolean tableCreated = false;

    public SQLAuthDAO() throws DataAccessException {
        super(tableCreated);
        tableCreated = true;
    }

    @Override
    protected String getTableName() {
        return "auth";
    }

    @Override
    public AuthData getAuth(String token) throws DataAccessException {
        return trySingleQuery("authToken", token, rs -> new AuthData(
                rs.getString("username"),
                rs.getString("authToken"))
        );
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        String token = UUID.randomUUID().toString();
        tryUpdate("INSERT INTO auth (authToken, username) VALUES (?, ?)", SQLDAO::confirmUpdate, token, username);
        return new AuthData(username, token);
    }

    @Override
    public void deleteAuth(String token) throws DataAccessException {
        tryUpdate("DELETE FROM auth WHERE authToken=?", SQLDAO::confirmUpdate, token);
    }

    @Override
    protected String getTableSetup() {
        return """
                CREATE TABLE IF NOT EXISTS auth (
                  `authToken` varchar(255) NOT NULL,
                  `username` varchar(255) NOT NULL,
                  PRIMARY KEY (`authToken`),
                  INDEX (username)
                )
                """;
    }

    public static AuthDAO getInstance() throws DataAccessException {
        return instance == null ? (instance = new SQLAuthDAO()) : instance;
    }
}
