package dataaccess.memory;

import model.dataaccess.AuthData;

import java.util.UUID;
import dataaccess.DataAccessObject.*;

public class MemoryAuthDAO extends MemoryDAO<AuthData> implements AuthDAO {
    @Override
    public AuthData getAuth(String token) {
        return get(AuthData::authToken, token);
    }

    @Override
    public AuthData createAuth(String username) {
        return add(new AuthData(username, UUID.randomUUID().toString()));
    }

    @Override
    public void deleteAuth(String token) {
        remove(AuthData::authToken, token);
    }
}
