package chess.calculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessGame.*;

public class PawnMoveCalculator implements PieceMoveCalculator {
    protected int getAxes() {
        return 2;
    }

    protected boolean isContinuous() {
        return false;
    }

    protected IntTuple getEndOffset(ChessBoard board, ChessPosition start, boolean... flips) {
        boolean straight = flips[0],
                mirror = flips[1];
        TeamColor color = board.getPiece(start).color();
        IntTuple off = new IntTuple(
                getTeamDirection(color),
                mirror ? -1 : 1);

        if (straight) {
            off = off.flatten();
            if (mirror) {
                if (start.getRow() != getTeamInitialRow(color) + off.a() ||
                        board.getPiece(off.newPosition(start)) != null) {
                    return null;
                }
                off = off.doubleA();
            }
        }
        return off;
    }

    private static final ChessPiece.PieceType[] promotions = {
            ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.KNIGHT,
            ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.ROOK
    };

    private static final ChessPiece.PieceType[] none = { null };

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

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition start) {
        Collection<ChessMove> moves = new ArrayList<>();
        int axes = getAxes();
        int perms = 1 << axes;

        boolean[] flips = new boolean[axes];

        for (int perm = 0; perm < perms; perm++) {
            for (int axis = 0, p = perm; axis < axes; axis++, p >>= 1) {
                flips[axis] = (p & 1) != 0;
            }

            boolean continuous = isContinuous();
            for (int i = 1; continuous || i <= 1; i++) {
                IntTuple endOffset = getEndOffset(board, start, flips);
                if (endOffset == null) {
                    break;
                }
                ChessPosition end = endOffset.newPosition(start);
                if (end.outOfBounds()) {
                    break;
                }
                ChessPiece atEnd = board.getPiece(end);
                if (atEnd != null && atEnd.color() == board.getPiece(start).color()) {
                    break;
                }
                // either is null or is opponent
                if (!tryAdd(moves, board, start, end)) {
                    break;
                }
            }
        }
        return moves;
    }
}
