package server.websocket.commands;

import chess.ChessGame;
import model.dataaccess.GameData;
import server.websocket.WebsocketException;
import service.ServiceException;
import server.websocket.Connection;
import service.GameService;
import service.UserService;
import websocket.commands.UserGameCommand;

public abstract class WSChessCommand<T extends UserGameCommand> extends WebSocketCommand<T> {
    protected static final String UNAUTHORIZED = "You are unauthorized.";

    protected String endGame(UserGameCommand command, ChessGame game) throws ServiceException {
        game.endGame();
        GameService.updateGameState(command, game);
        return "The game has ended.\n";
    }

    protected String checkConnection(String authToken) throws WebsocketException {
        if (!(connectionManager.getFromUsers(authToken) instanceof Connection connection)) {
            throw new WebsocketException(UNAUTHORIZED);
        }
        return connection.username();
    }

    protected GameData checkPlayerGameState(UserGameCommand command, String username, String description) throws ServiceException {
        boolean checkColor = !description.equals("resign");
        UserService.validateAuth(command.getAuthToken());
        GameData gameData = GameService.getGame(command.getGameID());
        if (gameData.game().isGameOver()) {
            throw new WebsocketException("Game is already finished. You cannot " + description + " anymore.");
        }
        ChessGame.TeamColor color = switch (username) {
            case String w when w.equals(gameData.whiteUsername()) -> ChessGame.TeamColor.WHITE;
            case String b when b.equals(gameData.blackUsername()) -> ChessGame.TeamColor.BLACK;
            default -> throw new WebsocketException("You need to be a player to " + description + ".");
        };
        if (checkColor && color != gameData.game().getTeamTurn()) {
            throw new WebsocketException("It is not your turn to make a move.");
        }
        return gameData;
    }
}
