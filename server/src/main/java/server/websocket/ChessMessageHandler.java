package server.websocket;

import chess.ChessGame;
import model.dataaccess.GameData;
import service.exception.*;
import service.GameService;
import websocket.commands.UserGameCommand;

import static utils.Catcher.*;

public abstract class ChessMessageHandler<T extends UserGameCommand> extends MessageHandler<T> {
    public ChessMessageHandler(GameService gameService, ConnectionManager connectionManager) {
        super(gameService, connectionManager);
    }

    protected void endGame(UserGameCommand command, ChessGame game, String extendMessage) throws ServiceException {
        game.endGame();
        gameService.updateGameState(command, game);
        notifyGame(command.getGameID(), extendMessage + "\nThe game has ended.");
    }

    protected String checkConnection(String authToken) throws ServiceException {
        return connectionManager.getFromUsers(authToken) instanceof String username ?
                username : throwNew(UnauthorizedException.class);
    }

    protected GameData checkPlayerGameState(UserGameCommand command, String username, boolean isMakeMove) throws ServiceException {
        String description = isMakeMove ? "make a move" : "resign";
        GameData gameData = gameService.getGame(command.getGameID());

        return gameData.game().isGameOver() ?
                throwNewWs("Game is already finished. You cannot " + description + " anymore.") :
                getPlayerColor(username, gameData) instanceof ChessGame.TeamColor color ?
                    color != gameData.game().getTeamTurn() && isMakeMove ?
                    throwNewWs("It is not your turn to make a move.") : gameData :
                throwNewWs("You need to be a player to " + description + ".");
    }

    private <Ts> Ts throwNewWs(String message) throws WebsocketException {
        return throwNew(WebsocketException.class, message);
    }
}
