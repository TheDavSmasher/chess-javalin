package dataaccess.sql;

import dataaccess.DataAccessObject.*;
import dataaccess.DataAccessException;
import model.dataaccess.AuthData;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class SQLAuthDAO extends SQLDAO implements AuthDAO {
    public SQLAuthDAO(AtomicBoolean tableCreated) throws DataAccessException {
        super(tableCreated);
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
        tryInsert("authToken, username", SQLDAO::confirmUpdate, token, username);
        return new AuthData(username, token);
    }

    @Override
    public void deleteAuth(String token) throws DataAccessException {
        tryUpdate("DELETE FROM auth WHERE authToken=?", SQLDAO::confirmUpdate, token);
    }

    @Override
    protected String getTableSetup() {
        return """
                (
                  `authToken` varchar(255) NOT NULL,
                  `username` varchar(255) NOT NULL,
                  PRIMARY KEY (`authToken`),
                  INDEX (username)
                )
                """;
    }
}
