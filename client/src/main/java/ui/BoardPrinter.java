package ui;

import chess.ChessGame;
import chess.ChessPosition;

public interface BoardPrinter {
    void printChessBoard(ChessGame chessGame, ChessPosition start, boolean whiteBottom);
    void resetColor();
}
