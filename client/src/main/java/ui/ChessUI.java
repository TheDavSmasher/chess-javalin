package ui;

import chess.*;

import java.io.PrintStream;
import java.util.function.Consumer;

import static chess.ChessBoard.BOARD_SIZE;
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
        printRow(null, i -> {
            int boardCol = invertIf(whiteBottom, i);
            printSquare((char) ('a' + boardCol));
        });
    }

    private void drawChessRow(int col, ChessBoard board, Boolean[][] moves, boolean firstIsWhite, boolean whiteBottom) {
        int boardRow = invertIf(!whiteBottom, col);
        printRow(boardRow + 1, i -> {
            int boardCol = invertIf(whiteBottom, i);
            drawChessSquare(board.getPiece(boardRow, boardCol), moves[boardRow][boardCol], (i % 2 == 0) == firstIsWhite);
        });
    }

    private int invertIf(boolean invert, int i) {
        return invert ? i : BOARD_SIZE - i - 1;
    }

    private void printRowBorders(Object border) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
        printSquare(border);
    }

    private void printRow(Object borders, Consumer<Integer> rowPrinter) {
        printRowBorders(borders);
        for (int i = 0; i < BOARD_SIZE; i++) {
            rowPrinter.accept(i);
        }
        printRowBorders(borders);
        resetColor();
    }

    private void drawChessSquare(ChessPiece piece, Boolean moveSpot, boolean isWhite) {
        boolean isMove = moveSpot != null;

        if (isMove) {
            out.print(moveSpot ? SET_BG_COLOR_YELLOW : isWhite ? SET_BG_COLOR_GREEN : SET_BG_COLOR_DARK_GREEN);
            out.print(SET_TEXT_COLOR_BLACK);
        } else {
            out.print(isWhite ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK);
            if (piece != null) {
                out.print(piece.color() == ChessGame.TeamColor.WHITE ? SET_TEXT_COLOR_RED : SET_TEXT_COLOR_BLUE);
            }
        }
        printSquare(piece);
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
