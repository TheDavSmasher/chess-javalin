package chess.calculator;

import chess.*;
import utils.BooleanCombinations;

import java.util.ArrayList;
import java.util.Collection;

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

    private static final ChessPiece.PieceType[] promotions = {
            ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.KNIGHT,
            ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.ROOK
    };

    private static final ChessPiece.PieceType[] none = { null };

    protected boolean tryAdd(Collection<ChessMove> endMoves, ChessBoard board, ChessPosition start, ChessPosition end) {
        ChessPiece atEnd = board.getPiece(end);
        if ((start.getColumn() == end.getColumn()) != (atEnd == null)) {
            return false;
        }
        ChessPiece.PieceType[] pieces = end.getRow() == currentTeam.otherTeam().initialRow() ? promotions : none;
        for (var piece : pieces) {
            endMoves.add(new ChessMove(start, end, piece));
        }
        return true;
    }

    private ChessGame.TeamColor currentTeam;

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition start) {
        currentTeam = board.getPiece(start).getTeamColor();
        Collection<ChessMove> moves = new ArrayList<>();

        for (var perm : new BooleanCombinations(getAxes())) {
            do {
                IntTuple endOffset = getEndOffset(board, start, asArray(perm.values()));
                if (endOffset == null) {
                    break;
                }
                ChessPosition end = endOffset.newPosition(start);
                if (end.outOfBounds()) {
                    break;
                }
                ChessPiece atEnd = board.getPiece(end);
                if (atEnd != null && atEnd.getTeamColor() == currentTeam) {
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
