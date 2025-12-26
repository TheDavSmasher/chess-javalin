package chess.calculator;

import chess.*;
import utils.BooleanCombinations;

import java.util.ArrayList;
import java.util.Collection;

public abstract class SymmetricCalculator implements PieceMoveCalculator {
    protected IntTuple.Modifier startModifier() {
        return IntTuple::self;
    }

    protected abstract IntTuple.Modifier[] getAxes();

    protected int getCombinationCount() {
        return getAxes().length;
    }

    protected abstract boolean isContinuous();

    protected void addMoves(Collection<ChessMove> moves, ChessPosition start, ChessPosition end) {
        moves.add(new ChessMove(start, end));
    }

    protected IntTuple getEndOffset(BooleanCombinations.BoolCombination perm) {
        IntTuple endOffset = startModifier().apply(new IntTuple(1));
        for (var combination : perm.whereTrue()) {
            endOffset = getAxes()[combination.index()].apply(endOffset);
        }
        return endOffset;
    }

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition start) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor currentTeam = board.getPiece(start).getTeamColor();

        for (var perm : new BooleanCombinations(getCombinationCount())) {
            int i = 0;
            do {
                i++;
                ChessPosition end = getEndOffset(perm).mul(i).newPosition(start);
                if (end.outOfBounds()) {
                    break;
                }
                ChessPiece atEnd = board.getPiece(end);
                if (atEnd != null && atEnd.getTeamColor() == currentTeam) {
                    break;
                }
                addMoves(moves, start, end);
                if (atEnd != null) {
                    break;
                }
            } while (isContinuous());
        }
        return moves;
    }
}
