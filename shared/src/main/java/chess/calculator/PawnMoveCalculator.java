package chess.calculator;

import chess.*;
import utils.BooleanCombinations;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessGame.*;
import static utils.iter.SelfIterable.asArray;

public class PawnMoveCalculator implements PieceMoveCalculator {
    protected int getAxes() {
        return 2;
    }

    protected boolean isContinuous() {
        return false;
    }

    protected IntTuple getEndOffset(ChessBoard board, ChessPosition start, Boolean... flips) {
        boolean straight = flips[0],
                mirror = flips[1];
        TeamColor color = board.getPiece(start).getTeamColor();
        IntTuple off = new IntTuple(
                color.direction(),
                mirror ? -1 : 1);

        if (straight) {
            off = off.flatten();
            if (mirror) {
                if (start.getRow() != color.initialRow() + off.a() ||
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
        TeamColor color = board.getPiece(start).getTeamColor();
        ChessPiece atEnd = board.getPiece(end);
        if ((start.getColumn() == end.getColumn()) != (atEnd == null)) {
            return false;
        }
        ChessPiece.PieceType[] pieces = end.getRow() == color.otherTeam().initialRow() ? promotions : none;
        for (var piece : pieces) {
            endMoves.add(new ChessMove(start, end, piece));
        }
        return true;
    }

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition start) {
        Collection<ChessMove> moves = new ArrayList<>();

        for (var perm : new BooleanCombinations(getAxes())) {
            int i = 0;
            do {
                i++;
                IntTuple endOffset = getEndOffset(board, start, asArray(perm.values()));
                if (endOffset == null) {
                    break;
                }
                ChessPosition end = endOffset.newPosition(start);
                if (end.outOfBounds()) {
                    break;
                }
                ChessPiece atEnd = board.getPiece(end);
                if (atEnd != null && atEnd.getTeamColor() == board.getPiece(start).getTeamColor()) {
                    break;
                }
                // either is null or is opponent
                if (!tryAdd(moves, board, start, end)) {
                    break;
                }
            } while (isContinuous());
        }
        return moves;
    }
}
