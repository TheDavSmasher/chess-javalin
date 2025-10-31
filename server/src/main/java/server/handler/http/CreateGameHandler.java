package server.handler.http;

import model.request.CreateGameRequest;
import model.response.CreateGameResponse;
import service.ServiceException;
import service.GameService;

public class CreateGameHandler extends RequestHandler<CreateGameRequest, CreateGameResponse, GameService> {
    public CreateGameHandler(GameService service) {
        super(service);
    }

    @Override
    protected CreateGameResponse serviceCall(CreateGameRequest createGameRequest, String authToken) throws ServiceException {
        return service.createGame(createGameRequest, authToken);
    }

    @Override
    protected Class<CreateGameRequest> getRequestClass() {
        return CreateGameRequest.class;
    }
}
