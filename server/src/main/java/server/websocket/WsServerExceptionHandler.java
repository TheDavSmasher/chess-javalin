package server.websocket;

import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsExceptionHandler;
import org.jetbrains.annotations.NotNull;
import service.ServiceException;
import websocket.messages.ErrorMessage;

public class WsServerExceptionHandler implements WsExceptionHandler<ServiceException> {
    @Override
    public void handle(@NotNull ServiceException e, @NotNull WsContext wsContext) {
        wsContext.send(new ErrorMessage(e.getMessage()));
    }
}
