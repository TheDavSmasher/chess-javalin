package server.websocket;

import service.ServiceException;

public class WebsocketException extends ServiceException {
    public WebsocketException(String message) {
        super(message);
    }
}
