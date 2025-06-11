package server.websocket;

import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import server.websocket.commands.*;
import websocket.commands.UserGameCommand;

import java.io.IOException;

import static utils.Catcher.*;
import static utils.Serializer.serialize;

public class WSServer implements WsMessageHandler {
    @Override
    public void handleMessage(WsMessageContext context) {
        (switch (context.messageAsClass(UserGameCommand.class).getCommandType()) {
            case CONNECT -> new WSConnect();
            case MAKE_MOVE -> new WSMakeMove();
            case LEAVE -> new WSLeave();
            case RESIGN -> new WSResign();
        }).handle(context);
    }

    public static void send(Session session, Object message) {
        tryCatchDo(() -> session.getRemote().sendString(serialize(message)),
                IOException.class, e -> {});
    }
}
