package service;

import chess.ChessGame;
import model.dataaccess.GameData;
import model.request.CreateGameRequest;
import model.request.JoinGameRequest;
import model.response.CreateGameResponse;
import model.response.ListGamesResponse;
import websocket.commands.LeaveCommand;
import websocket.commands.UserGameCommand;

import static utils.Catcher.*;
import static utils.Serializer.serialize;

public class GameService extends Service {
    public static ListGamesResponse getAllGames(String authToken) throws ServiceException {
        return tryAuthorized(authToken, () -> new ListGamesResponse(gameDAO().listGames()));
    }

    public static CreateGameResponse createGame(CreateGameRequest request, String authToken) throws ServiceException {
        return tryAuthorized(authToken, () ->
                new CreateGameResponse(gameDAO().createGame(getValidParameters(request.gameName())).gameID()));
    }

    public static GameData getGame(int gameID) throws ServiceException {
        return tryCatch(() -> gameDAO().getGame(gameID) instanceof GameData data ? data : throwNew(BadRequestException.class));
    }

    public static Void joinGame(JoinGameRequest request, String authToken) throws ServiceException {
        return updateGameConnection(authToken, request.gameID(), true, (oldGame, username) ->
                (switch (getValidParameters(request.playerColor()).toUpperCase()) {
                    case "WHITE" -> oldGame.whiteUsername();
                    case "BLACK" -> oldGame.blackUsername();
                    default -> throwNew(BadRequestException.class);
                }) instanceof String gameUser && !username.equals(gameUser)
                        ? throwNew(PreexistingException.class) : request.playerColor()
        );
    }

    //region WebSocket
    public static void leaveGame(LeaveCommand command) throws ServiceException {
        updateGameConnection(command.getAuthToken(), command.getGameID(), false, (oldGame, username) ->
                oldGame.game().isGameOver() ? null
                        : username.equals(oldGame.whiteUsername()) ? "WHITE"
                        : username.equals(oldGame.blackUsername()) ? "BLACK"
                        : null
        );
    }

    public static void updateGameState(UserGameCommand command, ChessGame game) throws ServiceException {
        tryAuthorized(command.getAuthToken(), _ -> gameDAO().updateGameBoard(command.getGameID(), serialize(game)));
    }
    //endregion

    private interface GameJoinUpdate {
        String update(GameData oldGame, String username) throws ServiceException;
    }

    private static Void updateGameConnection(String authToken, int gameID, boolean isJoining, GameJoinUpdate color) throws ServiceException {
        return tryAuthorized(authToken, username -> {
            if (color.update(getGame(gameID), username) instanceof String playerColor) {
                gameDAO().updateGamePlayer(gameID, playerColor, isJoining ? username : null);
            }
        });
    }
}
