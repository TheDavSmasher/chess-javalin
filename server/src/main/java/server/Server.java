package server;

import dataaccess.DAOFactory;
import dataaccess.sql.SQLDAOFactory;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.json.JavalinGson;
import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import model.response.ErrorResponse;
import server.handler.http.*;
import server.handler.websocket.WebsocketMessageHandlerFactory;
import service.*;
import websocket.commands.UserGameCommand;

public class Server {

    private final Javalin javalin;

    private final WebsocketMessageHandlerFactory websocketMessageHandlers;

    public Server() {
        DAOFactory daoFactory = new SQLDAOFactory();
        AppService appService = new AppService(daoFactory);
        UserService userService = new UserService(daoFactory);
        GameService gameService = new GameService(daoFactory);
        websocketMessageHandlers = new WebsocketMessageHandlerFactory(gameService);

        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
            config.jsonMapper(new JavalinGson());
        });

        // Register your endpoints and exception handlers here.
        javalin.delete("/db", new ClearHandler(appService))
               .post("/user", new RegisterHandler(userService))
               .post("/session", new LoginHandler(userService))
               .delete("/session", new LogoutHandler(userService))
               .get("/game", new ListGameHandler(gameService))
               .post("/game", new CreateGameHandler(gameService))
               .put("/game", new JoinGameHandler(gameService))
               .exception(ServiceException.class, this::handleServerException)
               .ws("/ws", this::setupWebsocket)
               .wsException(ServiceException.class, this::handleWebsocketException);
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
        }).json(getErrorResponse(e));
    }

    private void setupWebsocket(WsConfig ws) {
        ws.onConnect(WsContext::enableAutomaticPings);
        ws.onMessage(this::handleWebsocketMessage);
    }

    private void handleWebsocketMessage(WsMessageContext context) throws ServiceException {
        websocketMessageHandlers.get(context.messageAsClass(UserGameCommand.class).getCommandType())
                .handleMessage(context);
    }

    private void handleWebsocketException(ServiceException e, WsContext wsContext) {
        wsContext.send(getErrorResponse(e));
    }

    private static ErrorResponse getErrorResponse(Exception e) {
        return new ErrorResponse("Error: " + e.getMessage());
    }
}