package server.websocket;

import io.javalin.websocket.WsContext;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private record Connection(String username, WsContext context) {}

    private final ConcurrentHashMap<String, Connection> userConnections = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, ArrayList<Connection>> connectionsToGames = new ConcurrentHashMap<>();

    public void addToGame(int gameID, String authToken, String username, WsContext context) {
        Connection newConnection = new Connection(username, context);
        connectionsToGames.putIfAbsent(gameID, new ArrayList<>());
        connectionsToGames.get(gameID).add(newConnection);
        userConnections.put(authToken, newConnection);
    }

    public void removeFromGame(int gameID, String authToken) {
        if (!(connectionsToGames.get(gameID) instanceof ArrayList<Connection> gameConnections)) return;

        gameConnections.remove(userConnections.get(authToken));
        if (gameConnections.isEmpty()) {
            connectionsToGames.remove(gameID);
        } else {
            connectionsToGames.put(gameID, gameConnections);
        }
        userConnections.remove(authToken);
    }

    public String getFromUsers(String authToken) {
        return userConnections.get(authToken == null ? "" : authToken) instanceof Connection conn
                ? conn.username : null;
    }

    public void notifyGame(int gameID, ServerMessage serverMessage, String authToken) {
        if (!(connectionsToGames.get(gameID) instanceof ArrayList<Connection> gameConnections)) return;

        gameConnections.removeIf(c -> !c.context.session.isOpen());

        String user = getFromUsers(authToken);
        for (Connection current : gameConnections) {
            if (current.username.equals(user)) continue;
            current.context.send(serverMessage);
        }
    }
}
