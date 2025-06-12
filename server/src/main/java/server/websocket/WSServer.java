package server.websocket;

import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import server.websocket.commands.*;
import websocket.commands.UserGameCommand;

public class WSServer implements WsMessageHandler {
    @Override
    public void handleMessage(WsMessageContext context) {
        (switch (context.messageAsClass(UserGameCommand.class).getCommandType()) {
            case CONNECT -> new WSConnect();
            case MAKE_MOVE -> new WSMakeMove();
            case LEAVE -> new WSLeave();
            case RESIGN -> new WSResign();
        }).handleMessage(context);
    }
}
