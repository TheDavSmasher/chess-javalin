package server.websocket.handler;

import utils.EnumObjectFactory;
import websocket.commands.UserGameCommand;

import static websocket.commands.UserGameCommand.CommandType;

public final class WebsocketMessageHandlerFactory extends EnumObjectFactory<CommandType, MessageHandler<? extends UserGameCommand>> {
    public WebsocketMessageHandlerFactory() {
        super(true);
    }

    @Override
    protected Class<CommandType> getKeyClass() {
        return CommandType.class;
    }

    @Override
    protected MessageHandler<? extends UserGameCommand> generateValue(CommandType key) {
        return switch (key) {
            case CONNECT -> new ConnectHandler();
            case MAKE_MOVE -> new MakeMoveHandler();
            case LEAVE -> new LeaveHandler();
            case RESIGN -> new ResignHandler();
        };
    }
}
