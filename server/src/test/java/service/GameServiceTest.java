package service;

import model.dataaccess.GameData;
import model.request.CreateGameRequest;
import model.request.JoinGameRequest;
import model.request.UserEnterRequest;
import model.response.CreateGameResponse;
import model.response.ListGamesResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.exception.BadRequestException;
import service.exception.ServiceException;
import service.exception.UnauthorizedException;

import java.util.ArrayList;

class GameServiceTest extends ServiceTests<GameService> {

    UserService userService = new UserService(daoFactory);
    String authToken;
    String wrongAuthToken = "not-an-auth-token";

    public GameServiceTest() {
        super(daoFactory -> new GameService(daoFactory));
    }

    @Override
    @BeforeEach
    public void setup() throws ServiceException {
        super.setup();
        authToken = userService.register(new UserEnterRequest("davhig22", "pass123", "davhig22@byu.edu")).authToken();
    }

    @Test
    public void listGamesTest() throws ServiceException {
        ListGamesResponse expected = new ListGamesResponse(new ArrayList<>());
        Assertions.assertEquals(expected, service.getAllGames(authToken));

        ArrayList<GameData> gamesList = new ArrayList<>();
        gamesList.add(GameData.testEmpty(1, "game_1"));
        expected = new ListGamesResponse(gamesList);
        service.createGame(new CreateGameRequest("game_1"), authToken);
        Assertions.assertEquals(expected, service.getAllGames(authToken));

        gamesList.add(GameData.testEmpty(2, "game_2"));
        gamesList.add(GameData.testEmpty(3, "game_3"));
        expected = new ListGamesResponse(gamesList);
        service.createGame(new CreateGameRequest("game_2"), authToken);
        service.createGame(new CreateGameRequest("game_3"), authToken);
        Assertions.assertEquals(expected, service.getAllGames(authToken));
    }

    @Test
    public void listGamesFail() {
        Assertions.assertThrows(UnauthorizedException.class, () -> service.getAllGames(wrongAuthToken));
    }

    @Test
    public void createGameTest() throws ServiceException {
        CreateGameRequest createGameRequest = new CreateGameRequest("gameName");
        Assertions.assertEquals(new CreateGameResponse(1), service.createGame(createGameRequest, authToken));

        createGameRequest = new CreateGameRequest("gameName");
        Assertions.assertEquals(new CreateGameResponse(2), service.createGame(createGameRequest, authToken));
    }

    @Test
    public void createGameFail() {
        CreateGameRequest createGameRequest = new CreateGameRequest("gameName");

        Assertions.assertThrows(UnauthorizedException.class, () -> service.createGame(createGameRequest, wrongAuthToken));

        CreateGameRequest nullRequest = new CreateGameRequest(null);
        Assertions.assertThrows(BadRequestException.class, () -> service.createGame(nullRequest, authToken));

        CreateGameRequest badRequest = new CreateGameRequest("");
        Assertions.assertThrows(BadRequestException.class, () -> service.createGame(badRequest, authToken));
    }

    @Test
    public void joinGameTest() throws ServiceException {
        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", 1);

        service.createGame(new CreateGameRequest("gameName"), authToken);

        Assertions.assertDoesNotThrow(() -> service.joinGame(joinGameRequest, authToken));
    }

    @Test
    public void joinGameFail() throws ServiceException {
        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", 1);

        service.createGame(new CreateGameRequest("gameName"), authToken);
        Assertions.assertThrows(UnauthorizedException.class, () -> service.joinGame(joinGameRequest, wrongAuthToken));

        JoinGameRequest badColorRequest = new JoinGameRequest("yellow", 1);
        Assertions.assertThrows(BadRequestException.class, () -> service.joinGame(badColorRequest, authToken));

        JoinGameRequest nullGameRequest = new JoinGameRequest("WHITE", 2);
        Assertions.assertThrows(BadRequestException.class, () -> service.joinGame(nullGameRequest, authToken));
    }
}