package server.websocket;

import io.javalin.websocket.WsMessageContext;
import service.ServiceException;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.Notification;

public abstract class WebSocketCommand<T extends UserGameCommand> {
    protected static final ConnectionManager connectionManager = new ConnectionManager();

    protected abstract Class<T> getCommandClass();

    protected abstract void execute(T command, Session session) throws ServiceException;

    public void handle(WsMessageContext context) {
        try {
            execute(context.messageAsClass(getCommandClass()), context.session);
        } catch (ServiceException e) {
            WSServer.send(context.session, new ErrorMessage(e.getMessage()));
        }
    }

    protected void notifyGame(int gameID, String message) {
        notifyGame(gameID, null, message);
    }

    protected void notifyGame(int gameID, String authToken, String message) {
        connectionManager.notifyGame(gameID, new Notification(message), authToken);
    }
}
