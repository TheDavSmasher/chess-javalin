package server.handler.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import model.dataaccess.GameData;
import service.ServiceException;
import io.javalin.websocket.WsContext;
import service.GameService;
import websocket.commands.MakeMoveCommand;

import static utils.Catcher.*;

public class MakeMoveHandler extends ChessMessageHandler<MakeMoveCommand> {
    @Override
    protected Class<MakeMoveCommand> getCommandClass() {
        return MakeMoveCommand.class;
    }

    @Override
    protected void execute(MakeMoveCommand command, WsContext context) throws ServiceException {
        String username = checkConnection(command.getAuthToken());
        GameData gameData = checkPlayerGameState(command, username, true);

        ChessGame game = gameData.game();
        ChessMove move = command.getMove();
        tryCatchRethrow(() -> {
            game.makeMove(move); return null;
        }, InvalidMoveException.class, WebsocketException.class);

        GameService.updateGameState(command, game);

        loadGame(gameData, null);

        notifyGame(command.getGameID(), command.getAuthToken(), username + " has moved piece at " +
                move.getStartPosition() + " to " + move.getEndPosition() + ".");

        ChessGame.TeamColor currentTurn = game.getTeamTurn();
        ChessGame.CheckState state = game.getCheckState(currentTurn);
        if (state == ChessGame.CheckState.NONE) return;

        String opponent = (currentTurn == ChessGame.TeamColor.WHITE) ? gameData.whiteUsername() : gameData.blackUsername();
        String message = opponent + " is now in " + state.name().toLowerCase() + ".";
        if (state != ChessGame.CheckState.CHECK) {
            endGame(command, game, message + '\n' + (state == ChessGame.CheckState.STALEMATE ? "The game is tied." : username + " has won."));
        } else {
            notifyGame(command.getGameID(), message);
        }
    }
}
