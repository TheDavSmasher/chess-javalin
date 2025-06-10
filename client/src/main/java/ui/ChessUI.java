package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.io.PrintStream;
import java.util.Collection;

import static chess.ChessBoard.BOARD_SIZE;
import static java.lang.Character.isUpperCase;
import static ui.EscapeSequences.*;

public final class ChessUI {
    private final PrintStream out;

    public ChessUI(PrintStream out) {
        this.out = out;
    }

    public void printChessBoard(ChessGame chessGame, boolean whiteBottom) {
        printChessBoard(chessGame, null, whiteBottom);
    }

    public void printChessBoard(ChessGame chessGame, ChessPosition start, boolean whiteBottom) {
        out.println();
        printTopHeader(whiteBottom);

        Boolean[][] moves = new Boolean[BOARD_SIZE][BOARD_SIZE];
        if (start != null) {
            moves[start.getRow() - 1][start.getColumn() - 1] = true;

            Collection<ChessMove> pieceMoves = chessGame.validMoves(start);
            if (pieceMoves != null) {
                for (ChessMove move : pieceMoves) {
                    ChessPosition position = move.getEndPosition();
                    moves[position.getRow() - 1][position.getColumn() - 1] = false;
                }
            }
        }

        for (int i = 0; i < BOARD_SIZE; i++) {
            drawChessRow(i, chessGame.getBoard(), moves, i % 2 == 0, whiteBottom);
        }

        printTopHeader(whiteBottom);
    }

    private void printTopHeader(boolean whiteBottom) {
        setGreyBG();
        out.print("   ");
        for (int i = 0; i < BOARD_SIZE; i++) {
            out.print(" " + (char) ('a' + (whiteBottom ? i : BOARD_SIZE - i - 1)) + " ");
        }
        out.print("   ");
        resetColor();
        out.println();
    }

    private void drawSideHeader(int col, boolean whiteBottom) {
        setGreyBG();
        int actual = whiteBottom ? (BOARD_SIZE - col) : (col + 1);
        out.print(" " + actual + " ");
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
        out.println();
    }

    private void drawChessSquare(String pieceString, Boolean moveSpot, boolean isWhite) {
        boolean isMove = moveSpot != null;

        if (isMove) {
            out.print(SET_TEXT_COLOR_BLACK);
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
        out.print(pieceString != null ? " " + pieceString.toUpperCase() + " " : "   ");
    }

    private void setGreyBG() {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    public void resetColor() {
        out.print(UNSET_BG_COLOR);
        out.print(UNSET_TEXT_COLOR);
    }
}
