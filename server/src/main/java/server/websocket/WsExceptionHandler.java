package server.websocket;

import io.javalin.websocket.WsContext;
import org.jetbrains.annotations.NotNull;
import service.ServiceException;
import websocket.messages.ErrorMessage;

import static utils.Serializer.serialize;

public class WsExceptionHandler implements io.javalin.websocket.WsExceptionHandler<ServiceException> {
    @Override
    public void handle(@NotNull ServiceException e, @NotNull WsContext wsContext) {
        wsContext.send(serialize(new ErrorMessage(e.getMessage())));
    }
}
