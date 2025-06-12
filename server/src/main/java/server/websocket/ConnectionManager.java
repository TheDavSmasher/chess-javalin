package server.websocket;

import model.dataaccess.GameData;
import io.javalin.websocket.WsContext;
import websocket.messages.LoadGameMessage;
import websocket.messages.Notification;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import static utils.Serializer.serialize;

public class ConnectionManager {
    public record Connection(String username, WsContext context) {}

    private final ConcurrentHashMap<String, Connection> userConnections = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, ArrayList<Connection>> connectionsToGames = new ConcurrentHashMap<>();

    public void addToGame(GameData gameData, String authToken, String username, WsContext context) {
        Connection newConnection = new Connection(username, context);
        int gameID = gameData.gameID();
        if (!connectionsToGames.containsKey(gameID)) {
            connectionsToGames.put(gameID, new ArrayList<>());
        }
        connectionsToGames.get(gameID).add(newConnection);
        userConnections.put(authToken, newConnection);

        context.send(getLoadGame(gameData));
    }

    public void removeFromGame(int gameID, String authToken) {
        ArrayList<Connection> gameConnections = connectionsToGames.get(gameID);
        gameConnections.remove(userConnections.get(authToken));
        if (gameConnections.isEmpty()) {
            connectionsToGames.remove(gameID);
        } else {
            connectionsToGames.put(gameID, gameConnections);
        }
        userConnections.remove(authToken);
    }

    public String getFromUsers(String authToken) {
        return userConnections.get(authToken) instanceof Connection conn ? conn.username : null;
    }

    public void loadNewGame(GameData gameData) {
        LoadGameMessage message = getLoadGame(gameData);
        for (Connection current : connectionsToGames.get(gameData.gameID())) {
            current.context.send(message);
        }
    }

    private LoadGameMessage getLoadGame(GameData gameData) {
        return new LoadGameMessage(serialize(gameData.game()));
    }

    public void notifyGame(int gameID, Notification notification, String authToken) {
        ArrayList<Connection> gameConnections = connectionsToGames.get(gameID);
        if (gameConnections == null) return;

        gameConnections.removeIf(c -> !c.context.session.isOpen());

        Connection user = userConnections.get(authToken == null ? "" : authToken);
        for (Connection current : gameConnections) {
            if (current == user) continue;
            current.context.send(notification);
        }
    }
}
