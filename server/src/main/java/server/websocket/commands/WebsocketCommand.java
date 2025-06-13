package server.websocket.commands;

import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.dataaccess.GameData;
import org.jetbrains.annotations.NotNull;
import server.websocket.ConnectionManager;
import service.ServiceException;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import static utils.Serializer.serialize;

public abstract class WebsocketCommand<T extends UserGameCommand> implements WsMessageHandler {
    protected static final ConnectionManager connectionManager = new ConnectionManager();

    protected abstract Class<T> getCommandClass();

    protected abstract void execute(T command, WsContext context) throws ServiceException;

    @Override
    public void handleMessage(@NotNull WsMessageContext context) throws ServiceException {
        execute(context.messageAsClass(getCommandClass()), context);
    }

    public static LoadGameMessage getLoadGame(GameData gameData) {
        return new LoadGameMessage(serialize(gameData.game()));
    }

    protected void notifyGame(int gameID, String message) {
        notifyGame(gameID, null, message);
    }

    protected void notifyGame(int gameID, String authToken, String message) {
        notifyGame(gameID, authToken, new Notification(message));
    }

    protected void notifyGame(int gameID, String authToken, ServerMessage serverMessage) {
        connectionManager.notifyGame(gameID, serverMessage, authToken);
    }
}
