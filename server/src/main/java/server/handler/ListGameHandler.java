package server.handler;

public class ListGameHandler extends ObjectSerializer<ListGamesResponse> {
    @Override
    public ListGamesResponse serviceHandle(Context context) throws ServiceException {
        return GameService.getAllGames(getAuthToken(context));
    }
}
