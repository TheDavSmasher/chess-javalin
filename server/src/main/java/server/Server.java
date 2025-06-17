package server;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.json.JavalinGson;
import io.javalin.websocket.WsContext;
import model.response.ErrorResponse;
import server.handler.*;
import server.websocket.handler.*;
import service.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
            config.jsonMapper(new JavalinGson());
        });

        WebsocketMessageHandlerFactory websocketMessageHandlers = new WebsocketMessageHandlerFactory();

        // Register your endpoints and exception handlers here.
        javalin.delete("/db", new ClearHandler())
                .post("/user", new RegisterHandler())
                .post("/session", new LoginHandler())
                .delete("/session", new LogoutHandler())
                .get("/game", new ListGameHandler())
                .post("/game", new CreateGameHandler())
                .put("/game", new JoinGameHandler())
                .exception(ServiceException.class, this::handleServerException)
                .ws("/ws", ws -> {
                    ws.onConnect(WsContext::enableAutomaticPings);
                    ws.onMessage(context ->
                            websocketMessageHandlers.get(
                                            context.messageAsClass(UserGameCommand.class).getCommandType()
                                    ).handleMessage(context));
                }).wsException(ServiceException.class, this::handleWebsocketException);
    }

    public int run(int desiredPort) {
        return javalin.start(desiredPort).port();
    }

    public void stop() {
        javalin.stop();
    }

    private void handleServerException(ServiceException e, Context context) {
        context.status(switch (e) {
            case BadRequestException _ -> HttpStatus.BAD_REQUEST;
            case UnauthorizedException _ -> HttpStatus.UNAUTHORIZED;
            case PreexistingException _ -> HttpStatus.FORBIDDEN;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        }).json(new ErrorResponse("Error: " + e.getMessage()));
    }

    private void handleWebsocketException(ServiceException e, WsContext wsContext) {
        wsContext.send(new ErrorMessage(e.getMessage()));
    }
}