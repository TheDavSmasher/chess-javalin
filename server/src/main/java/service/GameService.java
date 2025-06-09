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
        return tryAuthorized(authToken, username -> {
            GameData oldGame = getGame(request.gameID());
            String color = getValidParameters(request.playerColor());

            if (!username.equals(playerColor(oldGame, color, false))) {
                throw new PreexistingException();
            }
            updateGamePlayer(request.gameID(), color, username);
        });
    }

    //region WebSocket
    public static void leaveGame(LeaveCommand command) throws ServiceException {
        tryAuthorized(command.getAuthToken(), username -> {
            GameData oldGame = getGame(command.getGameID());
            String color = playerColor(oldGame, username, true);

            if (oldGame.game().isGameOver()) return; //If game is over, keep names for legacy

            updateGamePlayer(command.getGameID(), color, null);
        });
    }

    public static void updateGameState(UserGameCommand command, ChessGame game) throws ServiceException {
        tryAuthorized(command.getAuthToken(), ignored -> gameDAO().updateGameBoard(command.getGameID(), serialize(game)));
    }
    //endregion

    private static String playerColor(GameData data, String player, boolean playerToColor) throws BadRequestException {
        if (playerToColor) {
            if (player.equals(data.whiteUsername())) return "WHITE";
            if (player.equals(data.blackUsername())) return "BLACK";
        } else {
            if (player.equalsIgnoreCase("WHITE")) return data.whiteUsername();
            if (player.equalsIgnoreCase("BLACK")) return data.blackUsername();
            throw new BadRequestException();
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
