package client;

import model.dataaccess.GameData;
import model.response.CreateGameResponse;
import org.junit.jupiter.api.*;
import server.Server;

import java.io.IOException;
import java.util.ArrayList;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private final String username = "davhig";
    private final String password = "passTest";
    private final String email = "davhig@gmeia.com";
    private final String wrongUser = "dabhif";
    private final String wrongPassword = "shall-not-pass";
    private final String wrongAuth = "not-an-auth";
    private final String gameName = "gameName";

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade(port);
    }

    @BeforeEach
    public void setUp() throws IOException {
        serverFacade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerTest() {
        Assertions.assertDoesNotThrow(() ->
                Assertions.assertNotNull(serverFacade.register(username, password, email).authToken()));
    }

    @Test
    public void registerFail() throws IOException {
        serverFacade.register(username, password, email);

        Assertions.assertThrows(IOException.class, () -> serverFacade.register("", password, email));
        Assertions.assertThrows(IOException.class, () -> serverFacade.register(username, "", email));
        Assertions.assertThrows(IOException.class, () -> serverFacade.register(username, "", email));
        Assertions.assertThrows(IOException.class, () -> serverFacade.register(username, password, email));
    }

    @Test
    public void LoginTest() {
        Assertions.assertDoesNotThrow(() -> serverFacade.register(username, password, email));

        Assertions.assertDoesNotThrow(() -> Assertions.assertNotNull(serverFacade.login(username, password).authToken()));
    }

    @Test
    public void LoginFail() throws IOException {
        Assertions.assertThrows(IOException.class, () -> serverFacade.login(username, password));

        serverFacade.register(username, password, email);

        Assertions.assertThrows(IOException.class, () -> serverFacade.login(username, wrongPassword));
        Assertions.assertThrows(IOException.class, () -> serverFacade.login(wrongUser, password));
    }

    @Test
    public void ListGamesTest() throws IOException {
        String auth = serverFacade.register(username, password, email).authToken();
        serverFacade.createGame(auth, gameName);
        ArrayList<GameData> expected = new ArrayList<>();
        expected.add(GameData.testEmpty(1, gameName));

        Assertions.assertEquals(expected, serverFacade.listGames(auth));

        serverFacade.createGame(auth, gameName);
        serverFacade.createGame(auth, gameName);
        expected.add(GameData.testEmpty(2, gameName));
        expected.add(GameData.testEmpty(3, gameName));

        Assertions.assertEquals(expected, serverFacade.listGames(auth));
    }

    @Test
    public void ListGamesFail() throws IOException {
        Assertions.assertThrows(IOException.class, () -> serverFacade.listGames(wrongAuth));

        String auth = serverFacade.register(username, password, email).authToken();

        Assertions.assertThrows(IOException.class, () -> serverFacade.listGames(wrongAuth));
        Assertions.assertTrue(serverFacade.listGames(auth).isEmpty());
    }

    @Test
    public void CreateGameTest() throws IOException {
        String auth = serverFacade.register(username, password, email).authToken();
        Assertions.assertEquals(new CreateGameResponse(1), serverFacade.createGame(auth, gameName));
        Assertions.assertEquals(new CreateGameResponse(2), serverFacade.createGame(auth, gameName));
    }

    @Test
    public void CreateGameFail() throws IOException {
        Assertions.assertThrows(IOException.class, () -> serverFacade.createGame(wrongAuth, gameName));
        String auth = serverFacade.register(username, password, email).authToken();
        Assertions.assertThrows(IOException.class, () -> serverFacade.createGame(wrongAuth, gameName));
        Assertions.assertThrows(IOException.class, () -> serverFacade.createGame(auth, ""));
    }

    @Test
    public void JoinGameTest() throws IOException {
        String auth = serverFacade.register(username, password, email).authToken();
        serverFacade.createGame(auth, gameName);
        Assertions.assertDoesNotThrow(() -> serverFacade.joinGame(auth, "white", 1));
    }

    @Test
    public void JoinGameFail() throws IOException {
        Assertions.assertThrows(IOException.class, () -> serverFacade.joinGame(wrongAuth, "white", 1));

        String auth = serverFacade.register(username, password, email).authToken();

        Assertions.assertThrows(IOException.class, () -> serverFacade.joinGame(auth, "white", 1));

        serverFacade.createGame(auth, gameName);

        Assertions.assertThrows(IOException.class, () -> serverFacade.joinGame(auth, "white", 0));
        Assertions.assertThrows(IOException.class, () -> serverFacade.joinGame(auth, "red", 1));

        serverFacade.joinGame(auth, "white", 1);

        Assertions.assertThrows(IOException.class, () -> serverFacade.joinGame(auth, "white", 0));
    }

    @Test
    public void LogoutTest() throws IOException {
        String tempAuth = serverFacade.register(username, password, email).authToken();

        Assertions.assertDoesNotThrow(() -> serverFacade.logout(tempAuth));

        String tempAuth2 = serverFacade.login(username,password).authToken();

        Assertions.assertDoesNotThrow(() -> serverFacade.logout(tempAuth2));
    }

    @Test
    public void LogoutFail() throws IOException {
        Assertions.assertThrows(IOException.class, () -> serverFacade.logout(wrongAuth));

        String tempAuth = serverFacade.register(username, password, email).authToken();
        serverFacade.logout(tempAuth);

        Assertions.assertThrows(IOException.class, () -> serverFacade.logout(tempAuth));
    }

    @Test
    public void ClearTest() {
        Assertions.assertDoesNotThrow(serverFacade::clear);
    }
}
