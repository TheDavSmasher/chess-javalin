package backend.websocket;

import chess.ChessMove;
import jakarta.websocket.*;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;

import static model.Serializer.deserialize;
import static model.Serializer.serialize;

public class WebsocketCommunicator extends Endpoint implements MessageHandler.Whole<String> {
    private Session session;
    private boolean connected = false;
    private final String socketUrl;
    private final ServerMessageObserver observer;

    public WebsocketCommunicator(String url, ServerMessageObserver messageObserver) {
        observer = messageObserver;
        socketUrl = url.replace("http", "ws");
    }

    private void connectToServer() throws IOException {
        try {
            session = ContainerProvider.getWebSocketContainer().connectToServer(this, URI.create(socketUrl + "ws"));
            session.addMessageHandler(this);
        } catch (DeploymentException e) {
            throw new IOException(e.getMessage());
        }
        connected = true;
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
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        //Method needed to call, but no functionality is required
    }

    public void connectToGame(String authToken, int gameID) throws IOException {
        sendCommand(new ConnectCommand(authToken, gameID));
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws IOException {
        sendCommand(new MakeMoveCommand(authToken, gameID, move));
    }

    public void leaveGame(String authToken, int gameID) throws IOException {
        sendCommand(new LeaveCommand(authToken, gameID));
    }

    public void resignGame(String authToken, int gameID) throws IOException {
        sendCommand(new ResignCommand(authToken, gameID));
    }

    private void sendCommand(UserGameCommand command) throws IOException {
        if (!connected) {
            connectToServer();
        }
        session.getBasicRemote().sendText(serialize(command));
    }
}
