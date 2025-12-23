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
        out.println(Format.resetAll());
    }

    private void printRowBorders(Object border) {
        printSquare(border, Color.LIGHT_GREY, Color.BLACK);
    }

    private void drawChessSquare(ChessPiece piece, Boolean moveSpot, boolean isWhite) {
        printSquare(piece,
                moveSpot != null ?
                        moveSpot ? Color.YELLOW : isWhite ? Color.GREEN: Color.DARK_GREEN :
                        isWhite ? Color.WHITE : Color.BLACK,
                moveSpot != null || piece == null ? Color.BLACK :
                        piece.getTeamColor() == ChessGame.TeamColor.WHITE ? Color.RED : Color.BLUE
                );
    }

    private void printSquare(Object value, Color background, Color textColor) {
        out.print(Format.BG.set(background) + Format.TEXT.set(textColor));
        out.print(" " + (value == null ? " " : value.toString().toUpperCase()) + " ");
    }
}
