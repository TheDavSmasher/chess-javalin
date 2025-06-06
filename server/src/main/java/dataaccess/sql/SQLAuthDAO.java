package dataaccess.sql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.dataaccess.AuthData;

import java.util.UUID;

public class SQLAuthDAO extends SQLDAO implements AuthDAO {
    private static SQLAuthDAO instance;

    public SQLAuthDAO() throws DataAccessException {}

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

    public static AuthDAO getInstance() throws DataAccessException {
        return instance == null ? (instance = new SQLAuthDAO()) : instance;
    }
}
