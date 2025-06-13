package server.websocket.handler;

import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import service.ServiceException;
import websocket.commands.UserGameCommand;

public class WsServerMessageHandler implements WsMessageHandler {
    @Override
    public void handleMessage(WsMessageContext context) throws ServiceException {
        (switch (context.messageAsClass(UserGameCommand.class).getCommandType()) {
            case CONNECT -> new ConnectHandler();
            case MAKE_MOVE -> new MakeMoveHandler();
            case LEAVE -> new LeaveHandler();
            case RESIGN -> new ResignHandler();
        }).handleMessage(context);
    }
}
