package server.websocket;

import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsExceptionHandler;
import org.jetbrains.annotations.NotNull;
import service.ServiceException;
import websocket.messages.ErrorMessage;

import static utils.Serializer.serialize;

public class WsServerExceptionHandler implements WsExceptionHandler<ServiceException> {
    @Override
    public void handle(@NotNull ServiceException e, @NotNull WsContext wsContext) {
        wsContext.send(serialize(new ErrorMessage(e.getMessage())));
    }
}
