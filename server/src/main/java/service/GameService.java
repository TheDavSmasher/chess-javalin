package service;

import chess.ChessGame;
import dataaccess.GameDAO;
import model.dataaccess.AuthData;
import model.dataaccess.GameData;
import model.request.CreateGameRequest;
import model.request.JoinGameRequest;
import model.response.CreateGameResponse;
import model.response.EmptyResponse;
import model.response.ListGamesResponse;

import static model.Serializer.serialize;
import static org.eclipse.jetty.util.StringUtil.isEmpty;
import static service.Service.tryCatch;

public class GameService {
    public static ListGamesResponse getAllGames(String authToken) throws ServiceException {
        return tryCatch(() -> {
            UserService.validateAuth(authToken);
            GameDAO gameDAO = GameDAO.getInstance();
            return new ListGamesResponse(gameDAO.listGames());
        });
    }

    public static CreateGameResponse createGame(CreateGameRequest request, String authToken) throws ServiceException {
        return tryCatch(() -> {
            UserService.validateAuth(authToken);
            if (isEmpty(request.gameName())) {
                throw new BadRequestException();
            }
            GameDAO gameDAO = GameDAO.getInstance();
            GameData newGame = gameDAO.createGame(request.gameName());
            return new CreateGameResponse(newGame.gameID());
        });
    }

    public static EmptyResponse joinGame(JoinGameRequest request, String authToken) throws ServiceException {
        return tryCatch(() -> {
            String username = UserService.validateAuth(authToken).username();
            GameDAO gameDAO = GameDAO.getInstance();
            GameData oldGame = gameDAO.getGame(request.gameID());
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

            gameDAO.updateGamePlayer(request.gameID(), color, username);
            return new EmptyResponse();
        });
    }

    public static GameData getGame(int gameID) throws ServiceException {
        return tryCatch(() -> GameDAO.getInstance().getGame(gameID));
    }

    //WebSocket
    public static void leaveGame(String authToken, int gameID) throws ServiceException {
        tryCatch(() -> {
            AuthData auth = UserService.validateAuth(authToken);
            GameDAO gameDAO = GameDAO.getInstance();
            GameData oldGame = gameDAO.getGame(gameID);
            if (oldGame == null) {
                throw new BadRequestException();
            }
            //If game is over, keep names for legacy
            if (oldGame.game().isGameOver()) return null;
            String color = switch (auth.username()) {
                case String w when w.equals(oldGame.whiteUsername()) -> "WHITE";
                case String b when b.equals(oldGame.blackUsername()) -> "BLACK";
                default -> "";
            };
            gameDAO.updateGamePlayer(!color.isEmpty() ? gameID : -1, color, null);
            return null;
        });
    }

    public static void updateGameState(String authToken, int gameID, ChessGame game) throws ServiceException {
        tryCatch(() -> {
            UserService.validateAuth(authToken);
            GameDAO.getInstance().updateGameBoard(gameID, serialize(game));
            return null;
        });
    }
}
