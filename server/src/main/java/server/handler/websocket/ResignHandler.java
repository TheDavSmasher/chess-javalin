package server.handler.websocket;

import chess.ChessGame;
import service.ServiceException;
import io.javalin.websocket.WsContext;
import websocket.commands.ResignCommand;

public class ResignHandler extends ChessMessageHandler<ResignCommand> {
    @Override
    protected Class<ResignCommand> getCommandClass() {
        return ResignCommand.class;
    }

    @Override
    protected void execute(ResignCommand command, WsContext context) throws ServiceException {
        String username = checkConnection(command.getAuthToken());
        ChessGame game = checkPlayerGameState(command, username, false).game();
        endGame(command, game, username + " has resigned the game.");
    }
}
