package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static chess.ChessBoard.BOARD_SIZE;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard gameBoard = new ChessBoard();
    private TeamColor currentTurn = TeamColor.WHITE;
    private boolean inPlay = true;

    public ChessGame() {
        gameBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTurn = team;
    }

    public boolean isGameOver() {
        return !inPlay;
    }

    public void endGame() {
        inPlay = false;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK;

        public TeamColor otherTeam() {
            return this == WHITE ? BLACK : WHITE;
        }

        public int direction() {
            return this == WHITE ? 1 : -1;
        }

        public int initialRow() {
            return this == WHITE ? 1 : 8;
        }
    }

    public enum CheckState {
        NONE,
        STALEMATE,
        CHECK,
        CHECKMATE
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        return gameBoard.getPiece(startPosition) instanceof ChessPiece temp
                ? removeInvalidMoves(temp.pieceMoves(gameBoard, startPosition)) : new ArrayList<>();
    }

    private Collection<ChessMove> removeInvalidMoves(Collection<ChessMove> moves) {
        moves.removeIf(move -> {
            ChessBoard testBoard = gameBoard.clone();
            TeamColor colorToCheck = gameBoard.getPiece(move.getStartPosition()).getTeamColor();
            makeMoveInGame(move, testBoard);
            return isInCheckTest(colorToCheck, testBoard);
        });
        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (!inPlay) {
            throw new InvalidMoveException("The game has already ended.");
        }
        if (!(gameBoard.getPiece(move.getStartPosition()) instanceof ChessPiece piece)) {
            throw new InvalidMoveException("There is no piece at the specified position.");
        }
        if (piece.getTeamColor() != currentTurn) {
            throw new InvalidMoveException("Piece not part of current turn's color.");
        }
        if (!(validMoves(move.getStartPosition()) instanceof Collection<ChessMove> validMoves)
                || !validMoves.contains(move)) {
            throw new InvalidMoveException("Move chosen is illegal.");
        }
        makeMoveInGame(move, gameBoard);
        gameBoard.getPiece(move.getEndPosition()).pieceMoved();
        setTeamTurn(currentTurn.otherTeam());
    }

    private void makeMoveInGame(ChessMove move, ChessBoard board) {
        ChessPiece oldPiece = board.getPiece(move.getStartPosition());
        if (move.getPromotionPiece() == null) {
            board.addPiece(move.getEndPosition(), oldPiece);
        } else {
            board.addPiece(move.getEndPosition(), new ChessPiece(oldPiece.getTeamColor(), move.getPromotionPiece()));
        }
        board.addPiece(move.getStartPosition(), null);
    }


    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isInCheckTest(teamColor, gameBoard);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return getCheckState(teamColor) == CheckState.CHECKMATE;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return getCheckState(teamColor) == CheckState.STALEMATE;
    }

    public CheckState getCheckState(TeamColor teamColor) {
        boolean isInCheck = isInCheck(teamColor);
        boolean hasNoMoves = removeInvalidMoves(allPossibleTeamMoves(teamColor, gameBoard)).isEmpty();

        if (isInCheck) {
            if (hasNoMoves)
                return CheckState.CHECKMATE;
            return CheckState.CHECK;
        }
        if (hasNoMoves)
            return CheckState.STALEMATE;
        return CheckState.NONE;
    }

    private boolean isInCheckTest(TeamColor teamColor, ChessBoard board) {
        Collection<ChessMove> allOpposingMoves = allPossibleTeamMoves(teamColor.otherTeam(), board);
        return allOpposingMoves.stream().anyMatch(move -> {
            ChessPiece temp = board.getPiece(move.getEndPosition());
            return temp != null && temp.getTeamColor() == teamColor && temp.getPieceType() == ChessPiece.PieceType.KING;
        });
    }

    private Collection<ChessMove> allPossibleTeamMoves(TeamColor team, ChessBoard board) {
        Collection<ChessMove> allMoves = new ArrayList<>();
        for (int i = 1; i <= BOARD_SIZE; i++) {
            for (int j = 1; j <= BOARD_SIZE; j++) {
                ChessPosition temp = new ChessPosition(i,j);
                ChessPiece atTemp = board.getPiece(temp);
                if (atTemp != null && atTemp.getTeamColor() == team) {
                    allMoves.addAll(atTemp.pieceMoves(board, temp));
                }
            }
        }
        return allMoves;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof ChessGame chessGame &&
                Objects.equals(gameBoard, chessGame.gameBoard) &&
                currentTurn == chessGame.currentTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameBoard, currentTurn);
    }
}
