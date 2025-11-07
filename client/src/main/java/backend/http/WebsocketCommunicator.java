package backend.http;

import backend.ServerFacade.ServerMessageObserver;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import java.io.IOException;
import java.net.URI;

import static utils.Catcher.*;
import static utils.Serializer.deserialize;
import static utils.Serializer.serialize;

public class WebsocketCommunicator extends Endpoint implements MessageHandler.Whole<String> {
    private Session session;
    private boolean connected = false;
    private final String socketUrl;
    private ServerMessageObserver observer;

    public WebsocketCommunicator(String url) {
        socketUrl = url.replace("http", "ws");
    }

    public void registerObserver(ServerMessageObserver observer) {
        this.observer = observer;
    }

    @Override
    public void onMessage(String message) {
        Class<? extends ServerMessage> messageClass =
            switch (deserialize(message, ServerMessage.class).getServerMessageType()) {
                case NOTIFICATION -> Notification.class;
                case LOAD_GAME    -> LoadGameMessage.class;
                case ERROR        -> ErrorMessage.class;
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
        if (connected && session != null) {
            if (session.isOpen()) {
                return;
            }
            observer.notify(new Notification("Reconnecting to server..."));
        }
        session = tryCatchRethrow(
                () -> ContainerProvider.getWebSocketContainer().connectToServer(this, URI.create(socketUrl + "ws")),
                DeploymentException.class, IOException.class);
        session.addMessageHandler(this);
        connected = true;
    }
}
