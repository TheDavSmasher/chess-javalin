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
    protected void addMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition start, ChessPosition end) {
        ChessGame.TeamColor currentTeam = board.getPiece(start).getTeamColor();
        if (end.getRow() != currentTeam.otherTeam().initialRow()) {
            super.addMoves(moves, board, start, end);
            return;
        }
        for (var piece : promotions) {
            moves.add(new ChessMove(start, end, piece));
        }
    }

    @Override
    protected IntTuple getEndOffset(IntTuple offset, ChessBoard board, ChessPosition start, Boolean... flips) {
        ChessGame.TeamColor currentTeam = board.getPiece(start).getTeamColor();
        boolean straight = flips[0],
                mirror = flips[1];
        if (currentTeam == ChessGame.TeamColor.BLACK) {
            offset = offset.swap().rotate();
        }
        if (straight) {
            offset = offset.flatten();
            if (mirror) {
                if (start.getRow() != currentTeam.initialRow() + offset.a() ||
                        board.getPiece(offset.newPosition(start)) != null) {
                    return null;
                }
                offset = offset.doubleA();
            }
        }
        if (mirror) {
            offset = offset.rotate().swap();
        }
        return offset;
    }

    @Override
    protected Boolean endLoopCheck(ChessPiece atEnd, Boolean... flips) {
        if (flips[0] != (atEnd == null)) {
            return null;
        }
        return false;
    }
}
