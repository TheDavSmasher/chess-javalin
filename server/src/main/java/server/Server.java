package server;

import io.javalin.Javalin;
import io.javalin.json.JavalinGson;
import io.javalin.websocket.WsContext;
import server.handler.*;
import server.websocket.WSServer;
import service.ServiceException;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
            config.jsonMapper(new JavalinGson());
        });

        // Register your endpoints and exception handlers here.
        javalin.delete("/db", new ClearHandler())
                .post("/user", new RegisterHandler())
                .post("/session", new LoginHandler())
                .delete("/session", new LogoutHandler())
                .get("/game", new ListGameHandler())
                .post("/game", new CreateGameHandler())
                .put("/game", new JoinGameHandler())
                .exception(ServiceException.class, new ExceptionHandler())
                .ws("/ws", ws -> {
                    ws.onConnect(WsContext::enableAutomaticPings);
                    ws.onMessage(new WSServer());
                });
    }

    public int run(int desiredPort) {
        return javalin.start(desiredPort).port();
    }

    public void stop() {
        javalin.stop();
    }
}