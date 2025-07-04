package dataaccess.memory;

import model.dataaccess.AuthData;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;
import dataaccess.DataAccessObject.*;

public class MemoryAuthDAO implements AuthDAO {
    static MemoryAuthDAO instance;
    private final HashSet<AuthData> data;

    private MemoryAuthDAO() {
        data = new HashSet<>();
    }

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

    @Override
    public void clear() {
        data.clear();
    }

    static public AuthDAO getInstance() {
        return instance == null ? (instance = new MemoryAuthDAO()) : instance;
    }
}
