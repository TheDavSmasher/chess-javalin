package server.websocket;

import service.GameService;
import utils.EnumObjectFactory;
import websocket.commands.UserGameCommand;

import static websocket.commands.UserGameCommand.CommandType;

public final class WebsocketMessageHandlerFactory extends EnumObjectFactory<CommandType, MessageHandler<? extends UserGameCommand>> {
    private final ConnectionManager connectionManager;
    private final GameService gameService;

    public WebsocketMessageHandlerFactory(GameService gameService, ConnectionManager connectionManager) {
        super(false);
        this.gameService = gameService;
        this.connectionManager = connectionManager;
        generateValues();
    }

    @Override
    protected Class<CommandType> getKeyClass() {
        return CommandType.class;
    }

    @Override
    protected MessageHandler<? extends UserGameCommand> generateValue(CommandType key) {
        return switch (key) {
            case CONNECT   -> new ConnectHandler(gameService, connectionManager);
            case MAKE_MOVE -> new MakeMoveHandler(gameService, connectionManager);
            case LEAVE     -> new LeaveHandler(gameService, connectionManager);
            case RESIGN    -> new ResignHandler(gameService, connectionManager);
        };
    }
}
