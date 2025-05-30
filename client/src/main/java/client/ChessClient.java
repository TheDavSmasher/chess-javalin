package client;

import backend.ServerFacade;
import backend.websocket.ServerMessageObserver;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
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

import static model.Serializer.deserialize;

public class ChessClient implements ServerMessageObserver {
    private String authToken = null;
    private int[] existingGames = null;
    private final PrintStream out;
    private int currentGameID = 0;
    private boolean whitePlayer = true;
    private MenuState currentState = MenuState.PRE_LOGIN;
    private ChessGame currentGame = null;
    private final ServerFacade serverFacade;

    public ChessClient(PrintStream out) {
        serverFacade = new ServerFacade(this);
        this.out = out;
    }

    public String evaluate(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        int command;
        try {
            command = (tokens.length > 0) ? Integer.parseInt(tokens[0]) : 0;
        } catch (NumberFormatException e) {
            help(false);
            return "Wrong option";
        }
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (currentState) {
            case PRE_LOGIN -> switch (command) {
                    case 1 -> register(params);
                    case 2 -> signIn(params);
                    case 3 -> "quit";
                    default -> help(false);
            };
            case POST_LOGIN ->  switch (command) {
                    case 1 -> listGames();
                    case 2 -> createGame(params);
                    case 3 -> joinGame(params);
                    case 4 -> observeGame(params);
                    case 5 -> logout();
                    default -> help(false);
            };
            case MID_GAME -> switch (command) {
                    case 1 -> redrawBoard();
                    case 2 -> makeMove(params);
                    case 3 -> highlightMoves(params);
                    case 4 -> leaveGame();
                    case 5 -> resignGame();
                    default -> help(false);
            };
            case OBSERVING -> switch (command) {
                    case 1 -> redrawBoard();
                    case 2 -> highlightMoves(params);
                    case 3 -> leaveGame();
                    default -> help(false);
            };
        };
    }

    private enum MenuState {
        PRE_LOGIN,
        POST_LOGIN,
        MID_GAME,
        OBSERVING
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

    public String help(boolean simple) {
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

        return "Helping";
    }

    private String register(String[] params) {
        if (params.length < 3) {
            out.print("Please provide a username, password, and email.\nFormat: 1 username password email");
            return "Retry";
        }
        String username = params[0], password = params[1], email = params[2];

        try {
            authToken = serverFacade.register(username, password, email).authToken();
            currentState = MenuState.POST_LOGIN;
        } catch (IOException e) {
            out.print(e.getMessage());
            return "Error Caught";
        }
        help(true);

        return "Welcome new user";
    }

    private String signIn(String[] params) {
        if (params.length < 2) {
            out.print("Please provide a username and password.\nFormat: 2 username password");
            return "Retry";
        }
        String username = params[0], password = params[1];

        try {
            authToken = serverFacade.login(username, password).authToken();
            currentState = MenuState.POST_LOGIN;
        } catch (IOException e) {
            out.print(e.getMessage());
            return "Error Caught";
        }
        help(true);

        return "Welcome back";
    }

    private String listGames() {
        ArrayList<GameData> allGames;
        try {
            allGames = serverFacade.listGames(authToken);
        } catch (IOException e) {
            out.print(e.getMessage());
            return "Error Caught";
        }
        existingGames = new int[allGames.size()];
        out.print("Games:");
        int i = 0;
        for (GameData data : allGames) {
            existingGames[i] = data.gameID();
            String white = (data.whiteUsername() != null) ? data.whiteUsername() : "No one";
            String black = (data.blackUsername() != null) ? data.blackUsername() : "No one";
            out.print("\n  " + (++i) + ". " + data.gameName() + ": " + white + " vs " + black);
        }
        return "Here's the games";
    }

    private String createGame(String[] params) {
        if (params.length < 1) {
            out.print("Please provide a game ID.\\nFormat: 2 gameName");
            return "Retry";
        }
        try {
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
        } catch (IOException e) {
            out.print(e.getMessage());
            return "Error Caught";
        }
        return "Created new game";
    }

    private String joinGame(String[] params) {
        if (params.length < 2) {
            out.print("Please provide a game ID and color.\nFormat: 3 WHITE/BLACK gameID");
            return "Retry";
        }
        if (existingGames == null) {
            out.print("Please list the games before you can join!");
            return "List first";
        }
        String color = params[0], gameIndex = params[1];
        try {
            int index = Integer.parseInt(gameIndex) - 1;
            if (index >= existingGames.length) {
                out.print("That game does not exist!");
                return "Out of range";
            }
            currentGameID = existingGames[index];
            serverFacade.joinGame(authToken, color, currentGameID);
            serverFacade.connectToGame(authToken, currentGameID);
            currentState = MenuState.MID_GAME;
            whitePlayer = color.equalsIgnoreCase("white");
        } catch (IOException e) {
            out.print(e.getMessage());
            return "Error Caught";
        } catch (NumberFormatException e) {
            help(false);
            return "Wrong option";
        }
        return "You joined";
    }

    private String observeGame(String[] params) {
        if (params.length < 1) {
            out.print("Please provide a game ID.\nFormat: 4 gameID");
            return "Retry";
        }
        if (existingGames == null) {
            out.print("Please list the games before you can join!");
            return "List first";
        }
        String gameIndex = params[0];
        try {
            int index = Integer.parseInt(gameIndex) - 1;
            if (index >= existingGames.length) {
                out.print("That game does not exist!");
                return "Out of range";
            }
            currentGameID = existingGames[index];
            serverFacade.connectToGame(authToken, currentGameID);
            currentState = MenuState.OBSERVING;
        } catch (IOException e) {
            out.print(e.getMessage());
            return "Error Caught";
        } catch (NumberFormatException e) {
            help(false);
            return "Wrong option";
        }
        return "You're now watching";
    }

    private String logout() {
        try {
            serverFacade.logout(authToken);
        } catch (IOException e) {
            out.print(e.getMessage());
            return "Error Caught";
        }
        authToken = null;
        help(true);

        return "See you later!";
    }

    private String redrawBoard() {
        String[][] gameBoard = ChessUI.getChessBoardAsArray(currentGame.getBoard());
        ChessUI.printChessBoard(out, gameBoard, whitePlayer);
        return "Board Printed";
    }

    private String makeMove(String[] params) {
        if (params.length < 2) {
            out.print("""
                Please provide a start and end position.
                If a pawn is to be promoted, also provide what it should become.
                Format: 2 start end (pieceType)""");
            return "Retry";
        }
        String start = params[0], end = params[1];
        try {
            ChessPiece.PieceType type = params.length < 3 ? null : typeFromString(params[2]);
            ChessMove move = new ChessMove(positionFromString(start), positionFromString(end), type);
            serverFacade.makeMove(authToken, currentGameID, move);
            return "Move made";
        } catch (IOException e) {
            out.print(e.getMessage());
            return "Retry";
        }
    }

    private String highlightMoves(String[] params) {
        if (params.length < 1) {
            out.print("Please provide a start position.\nFormat: 3 start");
        }
        String startPos = params[0];
        try {
            ChessPosition start = positionFromString(startPos);
            String[][] gameBoard = ChessUI.getChessBoardAsArray(currentGame.getBoard());
            String[][] moves = ChessUI.getValidMovesInArray((ArrayList<ChessMove>) currentGame.validMoves(start));
            ChessUI.printChessBoard(out, gameBoard, moves, whitePlayer);
            return "Valid Moves shown";
        } catch (IOException e) {
            out.print(e.getMessage());
            return "Retry";
        }
    }

    private String leaveGame() {
        try {
            serverFacade.leaveGame(authToken, currentGameID);
            currentGameID = 0;
            currentGame = null;
            currentState = MenuState.POST_LOGIN;
            help(true);
        } catch (IOException e) {
            out.print(e.getMessage());
            return "Caught Error";
        }
        return "Playing later";
    }

    private String resignGame() {
        try {
            serverFacade.resignGame(authToken, currentGameID);
            currentGameID = 0;
            currentGame = null;
            currentState = MenuState.POST_LOGIN;
            help(true);
        } catch (IOException e) {
            out.print(e.getMessage());
            return "Caught Error";
        }
        return "Sore Loser";
    }

    private ChessPosition positionFromString(String moveString) throws IOException {
        if (moveString.length() != 2) {
            throw new IOException("Wrong move format!");
        }
        char column = Character.toUpperCase(moveString.charAt(0));
        char row = moveString.charAt(1);
        int colInt = switch (column) {
            case 'A' -> 1;
            case 'B' -> 2;
            case 'C' -> 3;
            case 'D' -> 4;
            case 'E' -> 5;
            case 'F' -> 6;
            case 'G' -> 7;
            case 'H' -> 8;
            default -> throw new IOException("Column does not exist!");
        };
        int rowInt = Integer.parseInt(String.valueOf(row));
        if (rowInt > 8 || rowInt < 1) throw new IOException("Row does not exist!");
        return new ChessPosition(rowInt, colInt);
    }

    private ChessPiece.PieceType typeFromString(String type) throws IOException {
        try {
            return ChessPiece.PieceType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new IOException("That Piece Type does not exist.");
        }
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
        ChessUI.setRedText(out);
        out.println(errorMessage.getError());
        ChessUI.resetColor(out);
    }

    private void loadGame(LoadGameMessage message) {
        currentGame = deserialize(message.getGame(), ChessGame.class);
        ChessUI.printChessBoard(out, ChessUI.getChessBoardAsArray(currentGame.getBoard()), whitePlayer);
        help(true);
    }
}
