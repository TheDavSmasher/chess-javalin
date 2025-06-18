package client.states;

import java.io.IOException;
import java.util.ArrayList;

import client.ClientException;
import client.states.ClientCommandProcessing.*;
import model.dataaccess.GameData;

public class PostLoginClientState extends ChessClientState {
    private final ClientCommand[] stateCommands = {
            new ClientCommand(this::listGames, "List Games",
                    "show all games that are currently being hosted in the server."),
            new ClientCommand(this::createGame, "Create Game", 1,
                    "Please provide a game ID", "gameName",
                    "create a new game in the database with the given name.",
                    "The game's name can include spaces."),
            new ClientCommand(this::joinGame, "Join Game",2,
                    "Please provide a game ID and color", "[WHITE|BLACK] gameID",
                    "join an existing game with as a specific player color."),
            new ClientCommand(this::observeGame, "Observe Game", 1,
                    "Please provide a game ID", "gameID",
                    "see the current state of a game without becoming a player."),
            new ClientCommand(this::logout, "Logout",
                    "leave your current session and return to login menu.")
    };

    private int[] existingGames = null;

    public PostLoginClientState(ClientStateManager client) {
        super(client);
    }

    @Override
    protected ClientCommand[] getStateCommands() {
        return stateCommands;
    }

    private void listGames() throws IOException {
        ArrayList<GameData> allGames = client.serverFacade.listGames(client.getAuthToken());
        existingGames = new int[allGames.size()];
        client.out.print("Games:");
        int i = 0;
        for (GameData data : allGames) {
            existingGames[i] = data.gameID();
            String white = (data.whiteUsername() != null) ? data.whiteUsername() : "No one";
            String black = (data.blackUsername() != null) ? data.blackUsername() : "No one";
            client.out.print("\n  " + (++i) + ". " + data.gameName() + ": " + white + " vs " + black);
        }
    }

    private void createGame(String[] params) throws IOException {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            result.append(params[i]);
            if (i < params.length - 1) {
                result.append(" ");
            }
        }
        String fullName = result.toString();
        client.serverFacade.createGame(client.getAuthToken(), fullName);
        client.out.print("Game created! List all games to see it and be able to join or observe it.");
    }

    private void joinGame(String[] params) throws ClientException, IOException {
        enterGame(params[0], params[1]);
    }

    private void observeGame(String[] params) throws ClientException, IOException {
        enterGame(params[0], null);
    }

    private void enterGame(String gameIndex, String color) throws ClientException, IOException {
        if (existingGames == null) {
            throw new ClientException("Please list the games before you can join!");
        }
        int index = Integer.parseInt(gameIndex) - 1;
        if (index >= existingGames.length) {
            throw new ClientException("That game does not exist!");
        }
        int newGameID = existingGames[index];
        if (color != null) {
            client.serverFacade.joinGame(client.getAuthToken(), color, newGameID);
        }
        client.serverFacade.connectToGame(client.getAuthToken(), newGameID);
        client.enterGame(newGameID, color);
    }

    private void logout() throws IOException {
        client.serverFacade.logout(client.getAuthToken());
        client.logout();
    }
}
