package server.handler.http;

import model.request.JoinGameRequest;
import service.ServiceException;
import service.GameService;

public class JoinGameHandler extends RequestHandler<JoinGameRequest, Void, GameService> {
    public JoinGameHandler(GameService service) {
        super(service);
    }

    @Override
    protected Void serviceCall(JoinGameRequest joinGameRequest, String authToken) throws ServiceException {
        return service.joinGame(joinGameRequest, authToken);
    }

    @Override
    protected Class<JoinGameRequest> getRequestClass() {
        return JoinGameRequest.class;
    }
}
