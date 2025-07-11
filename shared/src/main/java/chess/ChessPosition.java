package chess;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public record ChessPosition(int row, int col) {
    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    public boolean outOfBounds() {
        return (row > 8 || row < 1) || (col > 8 || col < 1);
    }

    @NotNull
    @Override
    public String toString() {
        return String.valueOf((char) ('A' + col - 1)) + row;
    }
}