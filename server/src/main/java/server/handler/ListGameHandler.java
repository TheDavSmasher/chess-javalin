package server.handler;

import io.javalin.http.Context;
import model.response.ListGamesResponse;
import service.ServiceException;
import service.GameService;

public class ListGameHandler extends ObjectSerializer<ListGamesResponse> {
    @Override
    public ListGamesResponse serviceHandle(Context context) throws ServiceException {
        return GameService.getAllGames(getAuthToken(context));
    }
}
