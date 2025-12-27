package chess.calculator;

import chess.*;
import utils.BooleanCombinations;

import java.util.ArrayList;
import java.util.Collection;

import static utils.iter.SelfIterable.asArray;

public abstract class CombinationMoveCalculator implements PieceMoveCalculator {
    protected abstract int getCombinationCount();

    protected abstract IntTuple getEndOffset(IntTuple offset, ChessBoard board, ChessPosition start, Boolean... flips);

    protected abstract Boolean endLoopCheck(ChessPiece atEnd, Boolean... flips);

    protected void addMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition start, ChessPosition end) {
        moves.add(new ChessMove(start, end));
    }

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition start) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor currentTeam = board.getPiece(start).getTeamColor();

        for (var perm : new BooleanCombinations(getCombinationCount())) {
            Boolean[] offsets = asArray(perm.values());
            int i = 0;
            boolean moveCheck;
            do {
                i++;
                IntTuple endOffset = getEndOffset(new IntTuple(i), board, start, offsets);
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
                if (!(endLoopCheck(atEnd, offsets) instanceof Boolean s)) {
                    break;
                }
                addMoves(moves, board, start, end);
                if (atEnd != null) {
                    break;
                }
                moveCheck = s;
            } while(moveCheck);
        }
        return moves;
    }
}
