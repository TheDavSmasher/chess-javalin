package server.websocket;

import service.exception.ServiceException;

public class WebsocketException extends ServiceException {
    public WebsocketException(String message) {
        super(message);
    }
}
