package server.websocket.commands;

import chess.ChessGame;
import service.ServiceException;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.ResignCommand;

public class WSResign extends WSChessCommand<ResignCommand> {
    @Override
    protected Class<ResignCommand> getCommandClass() {
        return ResignCommand.class;
    }

    @Override
    protected void execute(ResignCommand command, Session session) throws ServiceException {
        String username = checkConnection(command.getAuthToken());
        ChessGame game = checkPlayerGameState(command, username, false).game();
        notifyGame(command.getGameID(), endGame(command, game) + username + " has resigned the game.");
    }
}
