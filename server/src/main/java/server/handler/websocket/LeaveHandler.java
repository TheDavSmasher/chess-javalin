package server.handler.websocket;

import service.ServiceException;
import io.javalin.websocket.WsContext;
import service.GameService;
import websocket.commands.LeaveCommand;

public class LeaveHandler extends ChessMessageHandler<LeaveCommand> {
    public LeaveHandler(GameService gameService) {
        super(gameService);
    }

    @Override
    protected Class<LeaveCommand> getCommandClass() {
        return LeaveCommand.class;
    }

    @Override
    protected void execute(LeaveCommand command, WsContext context) throws ServiceException {
        String username = checkConnection(command.getAuthToken());
        gameService.leaveGame(command);
        connectionManager.removeFromGame(command.getGameID(), command.getAuthToken());
        notifyGame(command.getGameID(), username + " has left the game.");
    }
}
