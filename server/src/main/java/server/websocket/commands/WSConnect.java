package server.websocket.commands;

import model.dataaccess.GameData;
import service.ServiceException;
import io.javalin.websocket.WsContext;
import service.GameService;
import service.UserService;
import websocket.commands.ConnectCommand;

public class WSConnect extends WebsocketCommand<ConnectCommand> {
    @Override
    protected Class<ConnectCommand> getCommandClass() {
        return ConnectCommand.class;
    }

    @Override
    protected void execute(ConnectCommand command, WsContext context) throws ServiceException {
        String username = UserService.validateAuth(command.getAuthToken());
        GameData data = GameService.getGame(command.getGameID());

        notifyGame(command.getGameID(), username + " is now observing the game.");
        context.send(getLoadGame(data));
        connectionManager.addToGame(data, command.getAuthToken(), username, context);
    }
}
