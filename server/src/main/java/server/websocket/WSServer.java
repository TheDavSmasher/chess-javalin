package server.websocket;

import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import server.websocket.commands.WSConnect;
import server.websocket.commands.WSLeave;
import server.websocket.commands.WSMakeMove;
import server.websocket.commands.WSResign;
import websocket.commands.UserGameCommand;

import java.io.IOException;

import static model.Serializer.serialize;

public class WSServer implements WsMessageHandler {
    @Override
    public void handleMessage(@NotNull WsMessageContext context) {
        UserGameCommand gameCommand = context.messageAsClass(UserGameCommand.class);
        (switch (gameCommand.getCommandType()) {
            case CONNECT -> new WSConnect();
            case MAKE_MOVE -> new WSMakeMove();
            case LEAVE -> new WSLeave();
            case RESIGN -> new WSResign();
        }).handle(context);
    }

    public static void send(Session session, Object message) {
        try {
            session.getRemote().sendString(serialize(message));
        } catch (IOException ignored) {}
    }
}
