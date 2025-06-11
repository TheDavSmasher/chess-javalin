package backend;

import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import java.io.IOException;
import java.net.URI;

import static utils.Serializer.deserialize;
import static utils.Serializer.serialize;
import static utils.Catcher.catchRethrow;

public class WebsocketCommunicator extends Endpoint implements MessageHandler.Whole<String> {
    private Session session;
    private boolean connected = false;
    private final String socketUrl;
    private final ServerMessageObserver observer;

    public WebsocketCommunicator(String url, ServerMessageObserver messageObserver) {
        observer = messageObserver;
        socketUrl = url.replace("http", "ws");
    }

    @Override
    public void onMessage(String message) {
        Class<? extends ServerMessage> messageClass =
            switch (deserialize(message, ServerMessage.class).getServerMessageType()) {
                case NOTIFICATION -> Notification.class;
                case LOAD_GAME -> LoadGameMessage.class;
                case ERROR -> ErrorMessage.class;
            };
        observer.notify(deserialize(message, messageClass));
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}

    public void sendCommand(UserGameCommand command) throws IOException {
        connectToServer();
        session.getBasicRemote().sendText(serialize(command));
    }

    private void connectToServer() throws IOException {
        if (connected) return;
        session = catchRethrow(
                () -> ContainerProvider.getWebSocketContainer().connectToServer(this, URI.create(socketUrl + "ws")),
                DeploymentException.class, IOException.class);
        session.addMessageHandler(this);
        connected = true;
    }
}
