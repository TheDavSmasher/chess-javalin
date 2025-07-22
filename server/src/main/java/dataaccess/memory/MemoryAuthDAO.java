package dataaccess.memory;

import model.dataaccess.AuthData;

import java.util.Objects;
import java.util.UUID;
import dataaccess.DataAccessObject.*;

public class MemoryAuthDAO extends MemoryDAO<AuthData> implements AuthDAO {
    @Override
    public AuthData getAuth(String token) {
        for (AuthData auth : data) {
            if (auth.authToken().equals(token)) {
                return auth;
            }
        }
        return null;
    }

    @Override
    public AuthData createAuth(String username) {
        String token = UUID.randomUUID().toString();
        AuthData newAuth = new AuthData(username, token);
        data.add(newAuth);
        return newAuth;
    }

    @Override
    public void deleteAuth(String token) {
        data.removeIf(user -> Objects.equals(user.authToken(), token));
    }
}
