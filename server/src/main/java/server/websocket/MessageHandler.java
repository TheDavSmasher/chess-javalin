package server.websocket;

import chess.ChessGame;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.dataaccess.GameData;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.exception.ServiceException;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import static utils.Serializer.serialize;

public abstract class MessageHandler<T extends UserGameCommand> implements WsMessageHandler {
    protected static final ConnectionManager connectionManager = new ConnectionManager();

    protected final GameService gameService;

    protected MessageHandler(GameService gameService) {
        this.gameService = gameService;
    }

    protected abstract Class<T> getCommandClass();

    protected abstract void execute(T command, WsContext context) throws ServiceException;

    @Override
    public void handleMessage(@NotNull WsMessageContext context) throws ServiceException {
        execute(context.messageAsClass(getCommandClass()), context);
    }

    protected ChessGame.TeamColor getPlayerColor(String user, GameData gameData) {
        return switch (user) {
            case String w when w.equals(gameData.whiteUsername()) -> ChessGame.TeamColor.WHITE;
            case String b when b.equals(gameData.blackUsername()) -> ChessGame.TeamColor.BLACK;
            default -> null;
        };
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

    protected void loadGame(GameData gameData, WsContext context) {
        LoadGameMessage loadMessage = new LoadGameMessage(serialize(gameData.game()));
        if (context != null) {
            context.send(loadMessage);
        }
        else {
            notifyGame(gameData.gameID(), null, loadMessage);
        }
    }
}
