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
    protected IntTuple getEndOffset(ChessBoard board, ChessPosition start, Boolean... flips) {
        ChessGame.TeamColor currentTeam = board.getPiece(start).getTeamColor();
        boolean straight = flips[0],
                mirror = flips[1];
        IntTuple off = new IntTuple(1);

        if (currentTeam == ChessGame.TeamColor.BLACK) {
            off = off.swap().rotate();
        }
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
        if (mirror) {
            off = off.rotate().swap();
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
