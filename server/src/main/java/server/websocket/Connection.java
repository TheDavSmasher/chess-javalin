package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

import static utils.Catcher.tryCatchDo;
import static utils.Serializer.serialize;

public record Connection(String username, Session session) {
    public void send(Object message) {
        tryCatchDo(() -> session.getRemote().sendString(serialize(message)),
                IOException.class, e -> {});
    }

    public boolean isOpen() {
        return session.isOpen();
    }
}
