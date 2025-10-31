package service;

import model.request.UserEnterRequest;
import model.response.UserEnterResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UserServiceTest extends ServiceTests<UserService> {

    String username = "davhig22";
    String password = "pass123";
    String email = "davhig22@byu.edu";
    String wrongUsername = "dabhig23";
    String wrongPassword = "shall-not-pass";
    UserEnterRequest enterRequest = new UserEnterRequest(username, password, email);
    String authToken;

    public UserServiceTest() {
        super(daoFactory -> new UserService(daoFactory));
    }

    @Test
    public void registerTest() throws ServiceException {
        UserEnterResponse response = service.register(enterRequest);
        Assertions.assertEquals(username, response.username());
        Assertions.assertNotNull(response.authToken());
    }

    @Test
    public void registerFail() throws ServiceException {
        UserEnterRequest badUsername = new UserEnterRequest("", password, email);
        Assertions.assertThrows(BadRequestException.class, () -> service.register(badUsername));

        UserEnterRequest badPassword = new UserEnterRequest(username, "", email);
        Assertions.assertThrows(BadRequestException.class, () -> service.register(badPassword));

        UserEnterRequest badEmail = new UserEnterRequest(username, password, "");
        Assertions.assertThrows(BadRequestException.class, () -> service.register(badEmail));

        service.register(enterRequest);
        Assertions.assertThrows(PreexistingException.class, () -> service.register(enterRequest));
    }

    @Test
    public void loginTest() throws ServiceException {
        service.register(enterRequest);
        Assertions.assertDoesNotThrow(() -> service.login(enterRequest));

        UserEnterResponse response = service.login(enterRequest);
        Assertions.assertEquals(username, response.username());
        Assertions.assertNotNull(response.authToken());
    }

    @Test
    public void loginFail() throws ServiceException {
        Assertions.assertThrows(UnauthorizedException.class, () -> service.login(enterRequest));

        service.register(enterRequest);

        UserEnterRequest wrongUser = new UserEnterRequest(wrongUsername, password);
        Assertions.assertThrows(UnauthorizedException.class, () -> service.login(wrongUser));

        UserEnterRequest wrongPass = new UserEnterRequest(username, wrongPassword);
        Assertions.assertThrows(UnauthorizedException.class, () -> service.login(wrongPass));
    }

    @Test
    public void logoutTest() throws ServiceException {
        authToken = service.register(enterRequest).authToken();
        Assertions.assertDoesNotThrow(() -> service.logout(authToken));

        authToken = service.login(enterRequest).authToken();
        Assertions.assertDoesNotThrow(() -> service.logout(authToken));
    }

    @Test
    public void logoutFail() throws ServiceException {
        authToken = "not-an-auth-token";
        Assertions.assertThrows(UnauthorizedException.class, () -> service.logout(authToken));

        authToken = service.register(enterRequest).authToken();
        service.logout(authToken);
        Assertions.assertThrows(UnauthorizedException.class, () -> service.logout(authToken));
    }

    @Test
    public void validUserTest() throws ServiceException {
        String wrongToken = "non-existent";
        Assertions.assertThrows(UnauthorizedException.class, () -> service.validateAuth(wrongToken));

        String authToken = service.register(enterRequest).authToken();
        String authUser = service.validateAuth(authToken);

        Assertions.assertEquals(username, authUser);
    }
}