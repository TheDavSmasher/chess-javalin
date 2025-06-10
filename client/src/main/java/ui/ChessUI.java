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
    public static void printChessBoard(PrintStream out, ChessBoard board, boolean whiteBottom) {
        printChessBoard(out, board, null, whiteBottom);
    }

    public static void printChessBoard(PrintStream out, ChessBoard chessBoard, Collection<ChessMove> pieceMoves, boolean whiteBottom) {
        out.println();
        printTopHeader(out, whiteBottom);

        String[][] moves = new String[BOARD_SIZE][BOARD_SIZE];
        if (pieceMoves != null) {
            boolean firstMove = true;
            for (ChessMove move : pieceMoves) {
                if (firstMove) {
                    ChessPosition start = move.getStartPosition();
                    moves[start.getRow() - 1][start.getColumn() - 1] = "S";
                    firstMove = false;
                }
                ChessPosition position = move.getEndPosition();
                moves[position.getRow() - 1][position.getColumn() - 1] = "V";
            }
        }

        for (int i = 0; i < BOARD_SIZE; i++) {
            drawChessRow(out, i, chessBoard, moves, i % 2 == 0, whiteBottom);
        }

        printTopHeader(out, whiteBottom);
    }

    private static void printTopHeader(PrintStream out, boolean whiteBottom) {
        String[] columns = { " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h "};
        setGreyBG(out);
        out.print("   ");
        for (int i = 0; i < BOARD_SIZE; i++) {
            out.print(columns[whiteBottom ? i : BOARD_SIZE - i - 1]);
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

    private static void drawChessRow(PrintStream out, int col, ChessBoard board, String[][] moves, boolean firstIsWhite, boolean whiteBottom) {
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

    private static void drawChessSquare(PrintStream out, String pieceString, String moveString, boolean isWhite) {
        boolean isMove = moveString != null;
        boolean moveSpot = !(isMove && "S".equals(moveString));

        if (isMove) {
            out.print(SET_TEXT_COLOR_BLACK);
            if (moveString.equals("S")) {
                out.print(SET_BG_COLOR_YELLOW);
            }
        }

        if (moveSpot) {
            out.print(isWhite
                    ? isMove ? SET_BG_COLOR_GREEN : SET_BG_COLOR_WHITE
                    : isMove ? SET_BG_COLOR_DARK_GREEN : SET_BG_COLOR_BLACK);
            if (pieceString != null) {
                out.print(isUpperCase(pieceString.charAt(0)) ? SET_TEXT_COLOR_RED : SET_TEXT_COLOR_BLUE);
            }
        }
        out.print(pieceString != null ? " "+pieceString.toUpperCase()+" " : "   ");
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
