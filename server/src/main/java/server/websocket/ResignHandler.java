package server.websocket;

import chess.ChessGame;
import service.GameService;
import service.exception.ServiceException;
import io.javalin.websocket.WsContext;
import websocket.commands.ResignCommand;

public class ResignHandler extends ChessMessageHandler<ResignCommand> {
    public ResignHandler(GameService gameService, ConnectionManager connectionManager) {
        super(gameService, connectionManager);
    }

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
