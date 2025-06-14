package server.websocket.handler;

import chess.ChessGame;
import model.dataaccess.GameData;
import service.ServiceException;
import io.javalin.websocket.WsContext;
import service.GameService;
import service.UserService;
import websocket.commands.ConnectCommand;

public class ConnectHandler extends MessageHandler<ConnectCommand> {
    @Override
    protected Class<ConnectCommand> getCommandClass() {
        return ConnectCommand.class;
    }

    @Override
    protected void execute(ConnectCommand command, WsContext context) throws ServiceException {
        String username = UserService.validateAuth(command.getAuthToken());
        GameData data = GameService.getGame(command.getGameID());

        String watchingAs = getPlayerColor(username, data) instanceof ChessGame.TeamColor color
                ? "playing as " + color.toString().toLowerCase() : "observing the game";
        notifyGame(command.getGameID(), username + " is now " + watchingAs);
        context.send(getLoadGame(data));
        connectionManager.addToGame(data.gameID(), command.getAuthToken(), username, context);
    }
}
