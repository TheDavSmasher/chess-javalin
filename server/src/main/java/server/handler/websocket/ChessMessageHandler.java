package server.handler.websocket;

import chess.ChessGame;
import model.dataaccess.GameData;
import service.ServiceException;
import service.GameService;
import websocket.commands.UserGameCommand;

import static utils.Catcher.*;

public abstract class ChessMessageHandler<T extends UserGameCommand> extends MessageHandler<T> {
    public ChessMessageHandler(GameService gameService) {
        super(gameService);
    }

    protected void endGame(UserGameCommand command, ChessGame game, String extendMessage) throws ServiceException {
        game.endGame();
        gameService.updateGameState(command, game);
        notifyGame(command.getGameID(), extendMessage + "\nThe game has ended.");
    }

    protected String checkConnection(String authToken) throws ServiceException {
        return connectionManager.getFromUsers(authToken) instanceof String username ?
                username : throwNew(WebsocketException.class, "You are unauthorized.");
    }

    protected GameData checkPlayerGameState(UserGameCommand command, String username, boolean isMakeMove) throws ServiceException {
        String description = isMakeMove ? "make a move" : "resign";

        GameData gameData = gameService.getGame(command.getGameID());

        return gameData.game().isGameOver() ?
                throwNew(WebsocketException.class, "Game is already finished. You cannot " + description + " anymore.") :
                getPlayerColor(username, gameData) instanceof ChessGame.TeamColor color ?
                    color != gameData.game().getTeamTurn() && isMakeMove ?
                    throwNew(WebsocketException.class, "It is not your turn to make a move.") : gameData :
                throwNew(WebsocketException.class,"You need to be a player to " + description + ".");
    }
}
