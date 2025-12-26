package chess.calculator;

import chess.*;
import utils.BooleanCombinations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public abstract class SymmetricCalculator implements PieceMoveCalculator {
    protected IntTuple.Modifier startModifier() {
        return IntTuple::self;
    }

    protected abstract IntTuple.Modifier[] getAxes();

    protected abstract boolean isContinuous();

    protected void addMoves(Collection<ChessMove> moves, ChessMove... newMoves) {
        moves.addAll(Arrays.asList(newMoves));
    }

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition start) {
        Collection<ChessMove> moves = new ArrayList<>();

        IntTuple.Modifier[] axes = getAxes();
        for (var perm : new BooleanCombinations(axes.length)) {
            int i = 0;
            do {
                i++;
                IntTuple endOffset = startModifier().apply(new IntTuple(i, i));
                for (var combination : perm.whereTrue()) {
                    endOffset = axes[combination.index()].apply(endOffset);
                }
                ChessPosition end = endOffset.newPosition(start);
                if (end.outOfBounds()) {
                    break;
                }
                ChessPiece atEnd = board.getPiece(end);
                if (atEnd == null || atEnd.getTeamColor() != board.getPiece(start).getTeamColor()) {
                    addMoves(moves, new ChessMove(start, end));
                }
                if (atEnd != null) {
                    break;
                }
            } while (isContinuous());
        }
        return moves;
    }
}
