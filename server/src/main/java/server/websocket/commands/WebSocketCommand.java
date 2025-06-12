package server.websocket.commands;

import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.jetbrains.annotations.NotNull;
import server.websocket.ConnectionManager;
import service.ServiceException;
import websocket.commands.UserGameCommand;
import websocket.messages.Notification;

public abstract class WebSocketCommand<T extends UserGameCommand> implements WsMessageHandler {
    protected static final ConnectionManager connectionManager = new ConnectionManager();

    protected abstract Class<T> getCommandClass();

    protected abstract void execute(T command, WsContext context) throws ServiceException;

    @Override
    public void handleMessage(@NotNull WsMessageContext context) throws ServiceException {
        execute(context.messageAsClass(getCommandClass()), context);
    }

    protected void notifyGame(int gameID, String message) {
        notifyGame(gameID, null, message);
    }

    protected void notifyGame(int gameID, String authToken, String message) {
        connectionManager.notifyGame(gameID, new Notification(message), authToken);
    }
}
