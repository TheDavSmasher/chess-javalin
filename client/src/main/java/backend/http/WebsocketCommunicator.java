package backend.http;

import backend.ServerFacade.ServerMessageObserver;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;

import static utils.Catcher.*;
import static utils.Serializer.*;

public class WebsocketCommunicator extends Endpoint implements MessageHandler.Whole<String> {
    private Session session;
    private boolean connected = false;
    private final String socketUrl;
    private final HashSet<ServerMessageObserver> observers = new HashSet<>();

    public WebsocketCommunicator(String url) {
        socketUrl = url.replace("http", "ws");
    }

    public void registerObserver(ServerMessageObserver observer) {
        observers.add(observer);
    }

    @Override
    public void onMessage(String message) {
        notifyAll(deserialize(message, ServerMessage.class));
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}

    private void notifyAll(ServerMessage message) {
        for (ServerMessageObserver observer : observers) {
            observer.notify(message);
        }
    }

    public void sendCommand(UserGameCommand command) throws IOException {
        connectToServer();
        session.getBasicRemote().sendText(serialize(command));
    }

    private void connectToServer() throws IOException {
        if (connected && session != null) {
            if (session.isOpen()) {
                return;
            }
            notifyAll(new Notification("Reconnecting to server..."));
        }
        session = tryCatchRethrow(
                () -> ContainerProvider.getWebSocketContainer().connectToServer(this, URI.create(socketUrl + "ws")),
                DeploymentException.class, IOException.class);
        session.addMessageHandler(this);
        connected = true;
    }
}
