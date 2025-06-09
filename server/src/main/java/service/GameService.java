package service;

import chess.ChessGame;
import model.dataaccess.GameData;
import model.request.CreateGameRequest;
import model.request.JoinGameRequest;
import model.response.CreateGameResponse;
import model.response.ListGamesResponse;
import websocket.commands.LeaveCommand;
import websocket.commands.UserGameCommand;

import static model.Serializer.serialize;
import static service.UserService.tryAuthorized;

public class GameService extends Service {
    public static ListGamesResponse getAllGames(String authToken) throws ServiceException {
        return tryAuthorized(authToken, () -> new ListGamesResponse(gameDAO().listGames()));
    }

    public static CreateGameResponse createGame(CreateGameRequest request, String authToken) throws ServiceException {
        return tryAuthorized(authToken, () ->
                new CreateGameResponse(gameDAO().createGame(getValidParameters(request.gameName())).gameID()));
    }

    public static GameData getGame(int gameID) throws ServiceException {
        return tryCatch(() -> {
            if (!(gameDAO().getGame(gameID) instanceof GameData data)) {
                throw new BadRequestException();
            }
            return data;
        });
    }

    public static Void joinGame(JoinGameRequest request, String authToken) throws ServiceException {
        return updateGameConnection(authToken, request.gameID(), true, (oldGame, username) ->
                gamePlayer(oldGame, getValidParameters(request.playerColor())) instanceof String gameUser
                        && !username.equals(gameUser) ? throwPreexisting() : request.playerColor());
    }

    //region WebSocket
    public static void leaveGame(LeaveCommand command) throws ServiceException {
        updateGameConnection(command.getAuthToken(), command.getGameID(), false, (oldGame, username) ->
                oldGame.game().isGameOver() ? null : playerColor(oldGame, username));
    }

    public static void updateGameState(UserGameCommand command, ChessGame game) throws ServiceException {
        tryAuthorized(command.getAuthToken(), ignored -> gameDAO().updateGameBoard(command.getGameID(), serialize(game)));
    }
    //endregion

    private static String gamePlayer(GameData data, String player) throws BadRequestException {
        if (player.equalsIgnoreCase("WHITE")) return data.whiteUsername();
        if (player.equalsIgnoreCase("BLACK")) return data.blackUsername();
        throw new BadRequestException();
    }

    private static String playerColor(GameData data, String player) {
        if (player.equals(data.whiteUsername())) return "WHITE";
        if (player.equals(data.blackUsername())) return "BLACK";
        return null;
    }

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
