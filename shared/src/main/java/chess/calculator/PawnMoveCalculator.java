package chess.calculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;

import static chess.ChessGame.*;

public class PawnMoveCalculator extends SymmetricCalculator {
    @Override
    protected int getAxes() {
        return 2;
    }

    @Override
    protected boolean isContinuous() {
        return false;
    }

    @Override
    protected IntTuple getEndOffset(ChessBoard board, ChessPosition start, int offset, boolean... flips) {
        boolean straight = flips[0],
                mirror = flips[1];
        TeamColor color = board.getPiece(start).color();
        IntTuple off = new IntTuple(
                getTeamDirection(color),
                boolMod(mirror));

        if (straight) {
            off = off.flatten();
            if (mirror) {
                if (start.getRow() != getTeamInitialRow(color) + off.x() ||
                        board.getPiece(newPosition(start, off)) != null) {
                    return null;
                }
                off = new IntTuple(off.x() * 2, off.y());
            }
        }
        return off;
    }

    private static final ChessPiece.PieceType[] promotions = {
            ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.KNIGHT,
            ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.ROOK
    };

    private static final ChessPiece.PieceType[] none = { null };

    @Override
    protected boolean tryAdd(Collection<ChessMove> endMoves, ChessBoard board, ChessPosition start, ChessPosition end) {
        TeamColor color = board.getPiece(start).color();
        ChessPiece atEnd = board.getPiece(end);
        if ((start.getColumn() == end.getColumn()) != (atEnd == null)) {
            return false;
        }
        ChessPiece.PieceType[] pieces = end.getRow() == getTeamInitialRow(getOtherTeam(color)) ? promotions : none;
        for (var piece : pieces) {
            endMoves.add(new ChessMove(start, end, piece));
        }
        return true;
    }
}
