package client;

import backend.ServerFacade;
import backend.ServerMessageObserver;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import utils.Catcher;
import model.dataaccess.GameData;
import ui.ChessUI;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

import static utils.Serializer.deserialize;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class ChessClient implements ServerMessageObserver {
    private String authToken = null;
    private int[] existingGames = null;
    private final PrintStream out;
    private int currentGameID = 0;
    private boolean whitePlayer = true;
    private MenuState currentState = MenuState.PRE_LOGIN;
    private ChessGame currentGame = null;
    private final ServerFacade serverFacade;
    private final ChessUI chessUI;

    public ChessClient(PrintStream out) {
        serverFacade = new ServerFacade(this);
        this.out = out;
        chessUI = new ChessUI(out);
    }

    public void evaluate(String input) throws ClientException {
        String[] tokens = input.toLowerCase().split(" ");
        try {
            int command = (tokens.length > 0) ? Integer.parseInt(tokens[0]) : 0;
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            switch (currentState) {
                case PRE_LOGIN -> {
                    switch (command) {
                        case 1 -> register(params);
                        case 2 -> signIn(params);
                        case 3 -> throw new ExitException();
                        default -> help(false);
                    }
                }
                case POST_LOGIN -> {
                    switch (command) {
                        case 1 -> listGames();
                        case 2 -> createGame(params);
                        case 3 -> joinGame(params);
                        case 4 -> observeGame(params);
                        case 5 -> logout();
                        default -> help(false);
                    }
                }
                case MID_GAME -> {
                    switch (command) {
                        case 1 -> redrawBoard();
                        case 2 -> makeMove(params);
                        case 3 -> highlightMoves(params);
                        case 4 -> leaveGame();
                        case 5 -> resignGame();
                        default -> help(false);
                    }
                }
                case OBSERVING -> {
                    switch (command) {
                        case 1 -> redrawBoard();
                        case 2 -> highlightMoves(params);
                        case 3 -> leaveGame();
                        default -> help(false);
                    }
                }
            }
        } catch (IOException e) {
            throw new ClientException(e.getMessage());
        } catch (NumberFormatException e) {
            help(false);
        }
    }

    private enum MenuState {
        PRE_LOGIN,
        POST_LOGIN,
        MID_GAME,
        OBSERVING
    }

    private void changeState(MenuState state) {
        currentState = state;
        if (state == MenuState.POST_LOGIN) {
            currentGame = null;
            currentGameID = 0;
        } else if (state == MenuState.PRE_LOGIN) {
            authToken = null;
        }
        help(true);
    }

    private void changeState(String token) {
        authToken = token;
        changeState(MenuState.POST_LOGIN);
    }

    private static String getHelpOption(String option, boolean skipDescription, String... description) {
        StringBuilder sb = new StringBuilder().append(option);
        if (!skipDescription) {
            sb.append(": ").append(description[0]);
            for (int i = 1; i < description.length; i++) {
                sb.append("\n   ").append(description[i]);
            }
        }
        return sb.append("\n").toString();
    }

    public void help(boolean simple) {
        out.println();

        StringBuilder sb = new StringBuilder();
        (switch (currentState) {
            case PRE_LOGIN -> sb
                    .append(getHelpOption("1 - Register", simple,
                            "creates a new user in the database. Username must be unique.",
                            "Format: 1 username password email"))
                    .append(getHelpOption("2 - Login", simple,
                            "logs in to the server with a pre-registered username with its corresponding password.",
                            "Format: 2 username password"))
                    .append(getHelpOption("3 - Quit", simple,
                            "exit out of the client."));
            case POST_LOGIN -> sb
                    .append(getHelpOption("1 - List Games", simple,
                            "show all games that are currently being hosted in the server."))
                    .append(getHelpOption("2 - Create Game", simple,
                            "create a new game in the database with a name. The game's name can include spaces.",
                            "Format: 2 gameName"))
                    .append(getHelpOption("3 - Join Game", simple,
                            "join an existing game with as a specific player color.",
                            "Format: 3 white/black gameID"))
                    .append(getHelpOption("4 - Observe Game", simple,
                            "see the current state of a game without becoming a player.",
                            "Format: 4 gameID"))
                    .append(getHelpOption("5 - Logout", simple,
                            "leave your current session and return to login menu."));
            case MID_GAME -> sb
                    .append(getHelpOption("1 - Redraw Board", simple,
                            "print the board again for the current state of the game."))
                    .append(getHelpOption("2 - Make Move", simple,
                            "select a piece in a given position and give its ending position.",
                            "Please make sure the move is legal.",
                            "Format: 2 start end        Format positions column then row, such as G6."))
                    .append(getHelpOption("3 - Highlight Legal Moves", simple,
                            "select a position on the board to see all legal moves the piece in that position can make.",
                            "Format: 3 position        Format positions column then row, such as G6."))
                    .append(getHelpOption("4 - Leave", simple,
                            "leave the current game, emptying your position and allowing anyone to join. Join again to continue."))
                    .append(getHelpOption("5 - Resign", simple,
                            "forfeit the current game, rendering it unplayable and the opposing player as winner.",
                            "This action cannot be undone."));
            case OBSERVING -> sb
                    .append(getHelpOption("1 - Redraw Board", simple,
                            "print the board again for the current state of the game."))
                    .append(getHelpOption("2 - Highlight Legal Moves", simple,
                            "select a position on the board to see all legal moves the piece in that position can make.",
                                    "Format: 3 position        Format positions column then row, such as G6."))
                    .append(getHelpOption("3 - Stop Watching", simple,
                            "leave the current game, returning to the menu."));
        }).append("\n").append(getHelpOption("0 - Help", simple,
                "print this menu again. Also prints out if input is beyond what's accepted."));
        out.print(sb);
    }

    private void register(String[] params) throws ClientException, IOException {
        if (params.length < 3) {
            throw new FormatException("Please provide a username, password, and email.", "1 username password email");
        }
        String username = params[0], password = params[1], email = params[2];
        changeState(serverFacade.register(username, password, email).authToken());
    }

    private void signIn(String[] params) throws ClientException, IOException {
        if (params.length < 2) {
            throw new FormatException("Please provide a username and password", "2 username password");
        }
        String username = params[0], password = params[1];
        changeState(serverFacade.login(username, password).authToken());
    }

    private void listGames() throws IOException {
        ArrayList<GameData> allGames = serverFacade.listGames(authToken);
        existingGames = new int[allGames.size()];
        out.print("Games:");
        int i = 0;
        for (GameData data : allGames) {
            existingGames[i] = data.gameID();
            String white = (data.whiteUsername() != null) ? data.whiteUsername() : "No one";
            String black = (data.blackUsername() != null) ? data.blackUsername() : "No one";
            out.print("\n  " + (++i) + ". " + data.gameName() + ": " + white + " vs " + black);
        }
    }

    private void createGame(String[] params) throws ClientException, IOException {
        if (params.length < 1) {
            throw new FormatException("Please provide a game ID", "2 gameName");
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            result.append(params[i]);
            if (i < params.length - 1) {
                result.append(" ");
            }
        }
        String fullName = result.toString();
        serverFacade.createGame(authToken, fullName);
        out.print("Game created! List all games to see it and be able to join or observe it.");
    }

    private void joinGame(String[] params) throws ClientException, IOException {
        if (params.length < 2) {
            throw new FormatException("Please provide a game ID and color", "3 WHITE/BLACK gameID");
        }
        if (existingGames == null) {
            throw new ClientException("Please list the games before you can join!");
        }
        String color = params[0], gameIndex = params[1];
        int index = Integer.parseInt(gameIndex) - 1;
        if (index >= existingGames.length) {
            throw new ClientException("That game does not exist!");
        }
        int newGameID = existingGames[index];
        serverFacade.joinGame(authToken, color, newGameID);
        serverFacade.connectToGame(authToken, newGameID);
        currentGameID = newGameID;
        whitePlayer = color.equalsIgnoreCase("white");
        changeState(MenuState.MID_GAME);
    }

    private void observeGame(String[] params) throws ClientException, IOException {
        if (params.length < 1) {
            throw new FormatException("Please provide a game ID","4 gameID");
        }
        if (existingGames == null) {
            throw new ClientException("Please list the games before you can join!");
        }
        String gameIndex = params[0];
        int index = Integer.parseInt(gameIndex) - 1;
        if (index >= existingGames.length) {
            throw new ClientException("That game does not exist!");
        }
        int newGameID = existingGames[index];
        serverFacade.connectToGame(authToken, currentGameID);
        currentGameID = newGameID;
        changeState(MenuState.OBSERVING);
    }

    private void logout() throws IOException {
        serverFacade.logout(authToken);
        changeState(MenuState.PRE_LOGIN);
    }

    private void redrawBoard() {
        chessUI.printChessBoard(currentGame, null, whitePlayer);
    }

    private void makeMove(String[] params) throws ClientException, IOException {
        if (params.length < 2) {
            throw new FormatException("""
                Please provide a start and end position.
                If a pawn is to be promoted, also provide what it should become.""",
                "2 start end (pieceType)");
        }
        String start = params[0], end = params[1];
        ChessPiece.PieceType type = params.length < 3 ? null : typeFromString(params[2]);
        ChessMove move = new ChessMove(positionFromString(start), positionFromString(end), type);
        serverFacade.makeMove(authToken, currentGameID, move);
    }

    private void highlightMoves(String[] params) throws ClientException, IOException {
        if (params.length < 1) {
            throw new FormatException("Please provide a start position.", "3 start");
        }
        String startPos = params[0];
        ChessPosition start = positionFromString(startPos);
        chessUI.printChessBoard(currentGame, start, whitePlayer);
    }

    private void leaveGame() throws IOException {
        serverFacade.leaveGame(authToken, currentGameID);
        changeState(MenuState.POST_LOGIN);
    }

    private void resignGame() throws IOException {
        serverFacade.resignGame(authToken, currentGameID);
        changeState(MenuState.POST_LOGIN);
    }

    private ChessPosition positionFromString(String moveString) throws IOException {
        if (moveString.length() != 2) {
            throw new IOException("Wrong move format!");
        }
        ChessPosition position = new ChessPosition(
                Character.getNumericValue(moveString.charAt(1)),
                Character.getNumericValue(moveString.charAt(0)) - 9 //A is 10
        );
        if (position.outOfBounds()) {
            throw new IOException("Position is invalid!");
        }
        return position;
    }

    private ChessPiece.PieceType typeFromString(String type) throws IOException {
        return Catcher.catchRethrow(() -> ChessPiece.PieceType.valueOf(type),
                IllegalArgumentException.class, IOException.class, _ -> "That Piece Type does not exist.");
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> displayNotification((Notification) message);
            case ERROR -> displayError((ErrorMessage) message);
            case LOAD_GAME -> loadGame((LoadGameMessage) message);
        }
    }

    private void displayNotification(Notification notification) {
        out.println(notification.getNotification());
    }

    private void displayError(ErrorMessage errorMessage) {
        out.print(SET_TEXT_COLOR_RED);
        out.println(errorMessage.getError());
        chessUI.resetColor();
    }

    private void loadGame(LoadGameMessage message) {
        currentGame = deserialize(message.getGame(), ChessGame.class);
        redrawBoard();
        help(true);
    }
}
