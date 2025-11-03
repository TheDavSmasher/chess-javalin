package server.websocket;

import service.GameService;
import utils.EnumObjectFactory;
import websocket.commands.UserGameCommand;

import static websocket.commands.UserGameCommand.CommandType;

public final class WebsocketMessageHandlerFactory extends EnumObjectFactory<CommandType, MessageHandler<? extends UserGameCommand>> {
    private final GameService gameService;

    public WebsocketMessageHandlerFactory(GameService gameService) {
        super(false);
        this.gameService = gameService;
        generateValues();
    }

    @Override
    protected Class<CommandType> getKeyClass() {
        return CommandType.class;
    }

    @Override
    protected MessageHandler<? extends UserGameCommand> generateValue(CommandType key) {
        return switch (key) {
            case CONNECT   -> new ConnectHandler(gameService);
            case MAKE_MOVE -> new MakeMoveHandler(gameService);
            case LEAVE     -> new LeaveHandler(gameService);
            case RESIGN    -> new ResignHandler(gameService);
        };
    }
}
