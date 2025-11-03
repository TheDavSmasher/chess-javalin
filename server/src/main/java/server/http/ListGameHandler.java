package server.http;

import io.javalin.http.Context;
import model.response.ListGamesResponse;
import service.exception.ServiceException;
import service.GameService;

public class ListGameHandler extends ResponseHandler<ListGamesResponse, GameService> {
    public ListGameHandler(GameService service) {
        super(service);
    }

    @Override
    public ListGamesResponse serviceHandle(Context context) throws ServiceException {
        return service.getAllGames(getAuthToken(context));
    }
}
