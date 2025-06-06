package server.websocket.commands;

import model.dataaccess.AuthData;
import model.dataaccess.GameData;
import server.websocket.WebsocketException;
import service.ServiceException;
import org.eclipse.jetty.websocket.api.Session;
import server.websocket.WebSocketCommand;
import service.GameService;
import service.UserService;
import websocket.commands.ConnectCommand;

public class WSConnect extends WebSocketCommand<ConnectCommand> {
    @Override
    protected Class<ConnectCommand> getCommandClass() {
        return ConnectCommand.class;
    }

    @Override
    protected void execute(ConnectCommand command, Session session) throws ServiceException {
        String username = UserService.validateAuth(command.getAuthToken());
        if (!(GameService.getGame(command.getGameID()) instanceof GameData data)) {
            throw new WebsocketException("Game does not exist.");
        }

        notifyGame(command.getGameID(), username + " is now observing the game.");
        connectionManager.addToGame(data, command.getAuthToken(), username, session);
    }
}
