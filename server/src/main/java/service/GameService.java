package service;

import chess.ChessGame;
import model.dataaccess.GameData;
import model.request.CreateGameRequest;
import model.request.JoinGameRequest;
import model.response.CreateGameResponse;
import model.response.ListGamesResponse;

import static model.Serializer.serialize;
import static org.eclipse.jetty.util.StringUtil.isEmpty;
import static service.UserService.tryAuthorized;

public class GameService extends Service {
    public static ListGamesResponse getAllGames(String authToken) throws ServiceException {
        return tryAuthorized(authToken, () -> new ListGamesResponse(gameDAO().listGames()));
    }

    public static CreateGameResponse createGame(CreateGameRequest request, String authToken) throws ServiceException {
        return tryAuthorized(authToken, () -> isEmpty(request.gameName()) ? throwBadRequest()
                : new CreateGameResponse(gameDAO().createGame(request.gameName()).gameID()));
    }

    public static Void joinGame(JoinGameRequest request, String authToken) throws ServiceException {
        return tryAuthorized(authToken, username -> {
            GameData oldGame = getGame(request.gameID());
            if (request.playerColor() == null || oldGame == null) {
                throw new BadRequestException();
            }
            String color = request.playerColor().toUpperCase();

            String gameUser = switch (color) {
                case "WHITE" -> oldGame.whiteUsername();
                case "BLACK" -> oldGame.blackUsername();
                default -> throw new BadRequestException();
            };

            if (!username.equals(gameUser)) {
                throw new PreexistingException();
            }

            gameDAO().updateGamePlayer(request.gameID(), color, username);
        });
    }

    public static GameData getGame(int gameID) throws ServiceException {
        return tryCatch(() -> gameDAO().getGame(gameID));
    }

    //region WebSocket
    public static void leaveGame(String authToken, int gameID) throws ServiceException {
        tryAuthorized(authToken, username -> {
            GameData oldGame = getGame(gameID);
            if (oldGame == null) {
                throw new BadRequestException();
            }
            //If game is over, keep names for legacy
            if (oldGame.game().isGameOver()) return;
            String color = switch (username) {
                case String w when w.equals(oldGame.whiteUsername()) -> "WHITE";
                case String b when b.equals(oldGame.blackUsername()) -> "BLACK";
                default -> "";
            };
            gameDAO().updateGamePlayer(!color.isEmpty() ? gameID : -1, color, null);
        });
    }

    public static void updateGameState(String authToken, int gameID, ChessGame game) throws ServiceException {
        tryAuthorized(authToken, ignored -> gameDAO().updateGameBoard(gameID, serialize(game)));
    }
    //endregion

    private void updateGamePlayer(int gameID, String playerColor) throws ServiceException {

    }
}
