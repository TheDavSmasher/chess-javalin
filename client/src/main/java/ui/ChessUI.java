package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import java.io.PrintStream;

import static chess.ChessBoard.BOARD_SIZE;
import static java.lang.Character.isUpperCase;
import static ui.EscapeSequences.*;

public final class ChessUI {
    private final PrintStream out;

    public ChessUI(PrintStream out) {
        this.out = out;
    }

    public void printChessBoard(ChessGame chessGame, ChessPosition start, boolean whiteBottom) {
        out.println();
        printTopHeader(whiteBottom);

        Boolean[][] moves = getMovesAsBooleans(chessGame, start);
        for (int i = 0; i < BOARD_SIZE; i++) {
            drawChessRow(i, chessGame.getBoard(), moves, i % 2 == 0, whiteBottom);
        }

        printTopHeader(whiteBottom);
    }

    private Boolean[][] getMovesAsBooleans(ChessGame chessGame, ChessPosition start) {
        Boolean[][] moves = new Boolean[BOARD_SIZE][BOARD_SIZE];
        if (start != null) {
            moves[start.getRow() - 1][start.getColumn() - 1] = true;

            for (ChessMove move : chessGame.validMoves(start)) {
                ChessPosition position = move.getEndPosition();
                moves[position.getRow() - 1][position.getColumn() - 1] = false;
            }
        }
        return moves;
    }

    private void printTopHeader(boolean whiteBottom) {
        setGreyBG();
        setBlackText();
        printSquare(null);
        for (int i = 0; i < BOARD_SIZE; i++) {
            printSquare((char) ('a' + (whiteBottom ? i : BOARD_SIZE - i - 1)));
        }
        printSquare(null);
        resetColor();
    }

    private void drawChessRow(int col, ChessBoard board, Boolean[][] moves, boolean firstIsWhite, boolean whiteBottom) {
        drawSideHeader(col, whiteBottom);
        int boardRow = whiteBottom ? (BOARD_SIZE - col - 1) : col;
        for (int i = 0; i < BOARD_SIZE; i++) {
            int boardCol = whiteBottom ? i : (BOARD_SIZE - i - 1);
            drawChessSquare(board.getPiece(boardRow, boardCol).toString(), moves[boardRow][boardCol], (i % 2 == 0) == firstIsWhite);
        }
        drawSideHeader(col, whiteBottom);
        resetColor();
    }

    private void drawSideHeader(int col, boolean whiteBottom) {
        setGreyBG();
        setBlackText();
        printSquare(whiteBottom ? (BOARD_SIZE - col) : (col + 1));
    }

    private void drawChessSquare(String pieceString, Boolean moveSpot, boolean isWhite) {
        boolean isMove = moveSpot != null;

        if (isMove) {
            setBlackText();
            if (moveSpot) {
                out.print(SET_BG_COLOR_YELLOW);
            }
        }

        if (!isMove || !moveSpot) {
            out.print(isWhite
                    ? isMove ? SET_BG_COLOR_GREEN : SET_BG_COLOR_WHITE
                    : isMove ? SET_BG_COLOR_DARK_GREEN : SET_BG_COLOR_BLACK);
            if (pieceString != null) {
                out.print(isUpperCase(pieceString.charAt(0)) ? SET_TEXT_COLOR_RED : SET_TEXT_COLOR_BLUE);
            }
        }
        printSquare(pieceString);
    }

    private void setGreyBG() {
        out.print(SET_BG_COLOR_LIGHT_GREY);
    }

    private void setBlackText() {
        out.print(SET_TEXT_COLOR_BLACK);
    }

    public void resetColor() {
        out.print(UNSET_BG_COLOR);
        out.print(UNSET_TEXT_COLOR);
        out.println();
    }

    private void printSquare(Object value) {
        out.print(" " + (value == null ? " " : value.toString().toUpperCase()) + " ");
    }
}
