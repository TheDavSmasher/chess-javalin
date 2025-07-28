package ui;

import chess.*;

import java.io.PrintStream;
import java.util.function.Consumer;

import static chess.ChessBoard.BOARD_SIZE;
import static ui.EscapeSequences.*;

public record ChessUI(PrintStream out) implements BoardPrinter {
    @Override
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
        printRow(null, col -> printRowBorders((char) ('a' + invertIf(whiteBottom, col))));
    }

    private void drawChessRow(int col, ChessBoard board, Boolean[][] moves, boolean firstIsWhite, boolean whiteBottom) {
        int boardRow = invertIf(!whiteBottom, col);
        printRow(boardRow + 1, i -> {
            int boardCol = invertIf(whiteBottom, i);
            drawChessSquare(board.getPiece(new ChessPosition(boardRow, boardCol)),
                    moves[boardRow][boardCol], (i % 2 == 0) == firstIsWhite);
        });
    }

    private int invertIf(boolean invert, int i) {
        return invert ? i : BOARD_SIZE - i - 1;
    }

    private void printRow(Object borders, Consumer<Integer> rowPrinter) {
        printRowBorders(borders);
        for (int i = 0; i < BOARD_SIZE; i++) {
            rowPrinter.accept(i);
        }
        printRowBorders(borders);
        resetColor();
    }

    private void printRowBorders(Object border) {
        printSquare(border, SET_BG_COLOR_LIGHT_GREY, SET_TEXT_COLOR_BLACK);
    }

    private void drawChessSquare(ChessPiece piece, Boolean moveSpot, boolean isWhite) {
        printSquare(piece,
                moveSpot != null ?
                        moveSpot ? SET_BG_COLOR_YELLOW : isWhite ? SET_BG_COLOR_GREEN : SET_BG_COLOR_DARK_GREEN :
                        isWhite ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK,
                moveSpot != null || piece == null ? SET_TEXT_COLOR_BLACK :
                        piece.color() == ChessGame.TeamColor.WHITE ? SET_TEXT_COLOR_RED : SET_TEXT_COLOR_BLUE
                );
    }

    @Override
    public void resetColor() {
        out.println(RESET_FORMATTING);
    }

    private void printSquare(Object value, String background, String textColor) {
        out.print(background + textColor);
        out.print(" " + (value == null ? " " : value.toString().toUpperCase()) + " ");
    }
}
