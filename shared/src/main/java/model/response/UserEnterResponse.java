package model.response;

import model.dataaccess.AuthData;

public record UserEnterResponse(String username, String authToken) {
    public static UserEnterResponse fromAuth(AuthData authData) {
        return new UserEnterResponse(authData.username(), authData.authToken());
    }
}
