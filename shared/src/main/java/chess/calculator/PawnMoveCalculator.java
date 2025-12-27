package chess.calculator;

import chess.*;

import java.util.Collection;

public class PawnMoveCalculator extends CombinationMoveCalculator {
    @Override
    protected int getCombinationCount() {
        return 2;
    }

    private static final ChessPiece.PieceType[] promotions = {
            ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.KNIGHT,
            ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.ROOK
    };

    private static final ChessPiece.PieceType[] none = { null };

    @Override
    protected void addMoves(Collection<ChessMove> moves, ChessPosition start, ChessPosition end) {
        ChessPiece.PieceType[] pieces = end.getRow() == currentTeam.otherTeam().initialRow() ? promotions : none;
        for (var piece : pieces) {
            moves.add(new ChessMove(start, end, piece));
        }
    }

    @Override
    protected IntTuple getEndOffset(ChessBoard board, ChessPosition start, Boolean... flips) {
        boolean straight = flips[0],
                mirror = flips[1];
        IntTuple off = new IntTuple(
                currentTeam.direction(),
                mirror ? -1 : 1);

        if (straight) {
            off = off.flatten();
            if (mirror) {
                if (start.getRow() != currentTeam.initialRow() + off.a() ||
                        board.getPiece(off.newPosition(start)) != null) {
                    return null;
                }
                off = off.doubleA();
            }
        }
        return off;
    }

    @Override
    protected Boolean endLoopCheck(ChessPiece atEnd, Boolean... flips) {
        if (flips[0] != (atEnd == null)) {
            return null;
        }
        return false;
    }
}
