package ui;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.io.PrintStream;
import java.util.Collection;

import static chess.ChessBoard.BOARD_SIZE;
import static java.lang.Character.isUpperCase;
import static ui.EscapeSequences.*;

public class ChessUI {
    public static void printChessBoard(PrintStream out, ChessBoard chessBoard, Collection<ChessMove> pieceMoves, boolean whiteBottom) {
        out.println();
        printTopHeader(out, whiteBottom);

        Boolean[][] moves = new Boolean[BOARD_SIZE][BOARD_SIZE];
        if (pieceMoves != null && !pieceMoves.isEmpty()) {
            ChessPosition start = pieceMoves.iterator().next().getStartPosition();
            moves[start.getRow() - 1][start.getColumn() - 1] = true;
            for (ChessMove move : pieceMoves) {
                ChessPosition position = move.getEndPosition();
                moves[position.getRow() - 1][position.getColumn() - 1] = false;
            }
        }

        for (int i = 0; i < BOARD_SIZE; i++) {
            drawChessRow(out, i, chessBoard, moves, i % 2 == 0, whiteBottom);
        }

        printTopHeader(out, whiteBottom);
    }

    private static void printTopHeader(PrintStream out, boolean whiteBottom) {
        setGreyBG(out);
        out.print("   ");
        for (int i = 0; i < BOARD_SIZE; i++) {
            out.print(" " + (char) ('a' + (whiteBottom ? i : BOARD_SIZE - i - 1)) + " ");
        }
        out.print("   ");
        resetColor(out);
        out.println();
    }

    private static void drawSideHeader(PrintStream out, int col, boolean whiteBottom) {
        setGreyBG(out);
        int actual = whiteBottom ? (BOARD_SIZE - col) : (col + 1);
        out.print(" " + actual + " ");
    }

    private static void drawChessRow(PrintStream out, int col, ChessBoard board, Boolean[][] moves, boolean firstIsWhite, boolean whiteBottom) {
        drawSideHeader(out, col, whiteBottom);
        int boardRow = whiteBottom ? (BOARD_SIZE - col - 1) : col;
        for (int i = 0; i < BOARD_SIZE; i++) {
            int boardCol = whiteBottom ? i : (BOARD_SIZE - i - 1);
            drawChessSquare(out, board.getPiece(boardRow, boardCol).toString(), moves[boardRow][boardCol], (i % 2 == 0) == firstIsWhite);
        }
        drawSideHeader(out, col, whiteBottom);
        resetColor(out);
        out.println();
    }

    private static void drawChessSquare(PrintStream out, String pieceString, Boolean moveSpot, boolean isWhite) {
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

    private static void setGreyBG(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    public static void resetColor(PrintStream out) {
        out.print(UNSET_BG_COLOR);
        out.print(UNSET_TEXT_COLOR);
    }
}
