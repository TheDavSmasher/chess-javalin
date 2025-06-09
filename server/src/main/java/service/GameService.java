package service;

import chess.ChessGame;
import model.dataaccess.GameData;
import model.request.CreateGameRequest;
import model.request.JoinGameRequest;
import model.response.CreateGameResponse;
import model.response.ListGamesResponse;

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
        return tryAuthorized(authToken, username -> {
            String color = getValidParameters(request.playerColor());
            GameData oldGame = getGame(request.gameID());

            if (!username.equals(getValidParameters(playerColor(oldGame, color.toUpperCase(), false)))) {
                throw new PreexistingException();
            }
            updateGamePlayer(request.gameID(), color, username);
        });
    }

    //region WebSocket
    public static void leaveGame(String authToken, int gameID) throws ServiceException {
        tryAuthorized(authToken, username -> {
            GameData oldGame = getGame(gameID);
            //If game is over, keep names for legacy
            if (oldGame.game().isGameOver()) return;

            String color = playerColor(oldGame, username, true);

            updateGamePlayer(gameID, color, null);
        });
    }

    public static void updateGameState(String authToken, int gameID, ChessGame game) throws ServiceException {
        tryAuthorized(authToken, ignored -> gameDAO().updateGameBoard(gameID, serialize(game)));
    }
    //endregion

    private static String playerColor(GameData data, String player, boolean playerToColor) {
        if (playerToColor) {
            if (player.equals(data.whiteUsername())) return "WHITE";
            if (player.equals(data.blackUsername())) return "BLACK";
        } else {
            if (player.equals("WHITE")) return data.whiteUsername();
            if (player.equals("BLACK")) return data.blackUsername();
        }
        return null;
    }

    private static void updateGamePlayer(int gameID, String playerColor, String username) throws ServiceException {
        tryCatch(() -> {
            if (!playerColor.isEmpty()) {
                gameDAO().updateGamePlayer(gameID, playerColor, username);
            }
            return null;
        });
    }
}
