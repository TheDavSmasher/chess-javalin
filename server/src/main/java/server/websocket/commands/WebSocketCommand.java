package server.websocket.commands;

import io.javalin.websocket.WsMessageContext;
import server.websocket.ConnectionManager;
import server.websocket.WSServer;
import service.ServiceException;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.Notification;

import static utils.Catcher.*;

public abstract class WebSocketCommand<T extends UserGameCommand> {
    protected static final ConnectionManager connectionManager = new ConnectionManager();

    protected abstract Class<T> getCommandClass();

    protected abstract void execute(T command, Session session) throws ServiceException;

    public void handle(WsMessageContext context) {
        tryCatchDo(() -> execute(context.messageAsClass(getCommandClass()), context.session),
                ServiceException.class, e -> WSServer.send(context.session, new ErrorMessage(e.getMessage())));
    }

    protected void notifyGame(int gameID, String message) {
        notifyGame(gameID, null, message);
    }

    protected void notifyGame(int gameID, String authToken, String message) {
        connectionManager.notifyGame(gameID, new Notification(message), authToken);
    }
}
