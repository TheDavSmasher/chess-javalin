package server.websocket.commands;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import model.dataaccess.GameData;
import server.websocket.WebsocketException;
import service.ServiceException;
import io.javalin.websocket.WsContext;
import service.GameService;
import websocket.commands.MakeMoveCommand;

import static utils.Catcher.*;

public class WSMakeMove extends WSChessCommand<MakeMoveCommand> {
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
            game.makeMove(move);
            return null;
        }, InvalidMoveException.class, WebsocketException.class);

        GameService.updateGameState(command, game);

        notifyGame(gameData.gameID(), null, getLoadGame(gameData));

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
