package server.websocket.commands;

import service.ServiceException;
import org.eclipse.jetty.websocket.api.Session;
import service.GameService;
import websocket.commands.LeaveCommand;

public class WSLeave extends WSChessCommand<LeaveCommand> {
    @Override
    protected Class<LeaveCommand> getCommandClass() {
        return LeaveCommand.class;
    }

    @Override
    protected void execute(LeaveCommand command, Session session) throws ServiceException {
        String username = checkConnection(command.getAuthToken());
        GameService.leaveGame(command.getAuthToken(), command.getGameID());
        connectionManager.removeFromGame(command.getGameID(), command.getAuthToken());
        notifyGame(command.getGameID(), username + " has left the game.");
    }
}
