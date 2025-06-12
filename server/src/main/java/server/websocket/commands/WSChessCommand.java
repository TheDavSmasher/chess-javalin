package server.websocket.commands;

import chess.ChessGame;
import model.dataaccess.GameData;
import server.websocket.WebsocketException;
import service.ServiceException;
import server.websocket.Connection;
import service.GameService;
import websocket.commands.UserGameCommand;

import static utils.Catcher.*;

public abstract class WSChessCommand<T extends UserGameCommand> extends WebSocketCommand<T> {
    protected String endGame(UserGameCommand command, ChessGame game) throws ServiceException {
        game.endGame();
        GameService.updateGameState(command, game);
        return "The game has ended.\n";
    }

    protected String checkConnection(String authToken) throws ServiceException {
        return connectionManager.getFromUsers(authToken) instanceof Connection connection ?
                connection.username() : throwNew(WebsocketException.class, "You are unauthorized.");
    }

    protected GameData checkPlayerGameState(UserGameCommand command, String username, boolean isMakeMove) throws ServiceException {
        String description = isMakeMove ? "make a move" : "resign";

        GameData gameData = GameService.getGame(command.getGameID());

        return gameData.game().isGameOver() ?
                throwNew(WebsocketException.class, "Game is already finished. You cannot " + description + " anymore.") :
                (switch (username) {
                    case String w when w.equals(gameData.whiteUsername()) -> ChessGame.TeamColor.WHITE;
                    case String b when b.equals(gameData.blackUsername()) -> ChessGame.TeamColor.BLACK;
                    default -> throwNew(WebsocketException.class,"You need to be a player to " + description + ".");
                }) != gameData.game().getTeamTurn() && isMakeMove ?
                throwNew(WebsocketException.class, "It is not your turn to make a move.") : gameData;
    }
}
