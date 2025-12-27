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

    @Override
    protected void addMoves(Collection<ChessMove> moves, ChessPosition start, ChessPosition end) {
        if (end.getRow() != currentTeam.otherTeam().initialRow()) {
            super.addMoves(moves, start, end);
            return;
        }
        for (var piece : promotions) {
            moves.add(new ChessMove(start, end, piece));
        }
    }

    @Override
    protected IntTuple getEndOffset(IntTuple offset, ChessBoard board, ChessPosition start, Boolean... flips) {
        boolean straight = flips[0],
                mirror = flips[1];
        if (currentTeam == ChessGame.TeamColor.BLACK) {
            offset = offset.mirrorA();
        }
        if (straight) {
            offset = offset.flatten();
            if (mirror) {
                if (start.getRow() != currentTeam.initialRow() + offset.a() ||
                        board.getPiece(offset.newPosition(start)) != null) {
                    return null;
                }
                return offset.doubleA();
            }
        }
        if (mirror) {
            offset = offset.mirrorB();
        }
        return offset;
    }

    @Override
    protected Boolean endLoopCheck(ChessBoard board, ChessPosition end, Boolean... flips) {
        if (flips[0] != (board.getPiece(end) == null)) {
            return null;
        }
        return false;
    }
}
