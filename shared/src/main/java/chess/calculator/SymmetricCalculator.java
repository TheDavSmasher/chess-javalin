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

    protected abstract boolean isContinuous();

    protected void addMoves(Collection<ChessMove> moves, ChessPosition start, ChessPosition end) {
        moves.add(new ChessMove(start, end));
    }

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition start) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor currentTeam = board.getPiece(start).getTeamColor();

        IntTuple.Modifier[] axes = getAxes();
        for (var perm : new BooleanCombinations(axes.length)) {
            int i = 0;
            do {
                i++;
                IntTuple endOffset = startModifier().apply(new IntTuple(i));
                for (var combination : perm.whereTrue()) {
                    endOffset = axes[combination.index()].apply(endOffset);
                }
                ChessPosition end = endOffset.newPosition(start);
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
