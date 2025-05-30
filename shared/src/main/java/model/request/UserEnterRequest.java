package model.request;

public record UserEnterRequest(String username, String password, String email) {
    public UserEnterRequest(String username, String password) {
        this(username, password, null);
    }
}
