package chess.calculator;

import chess.*;
import utils.BooleanCombinations;

import java.util.ArrayList;
import java.util.Collection;

import static utils.iter.SelfIterable.asArray;

public abstract class SymmetricCalculator implements PieceMoveCalculator {
    protected IntTuple.Modifier startModifier() {
        return IntTuple::self;
    }

    protected abstract IntTuple.Modifier[] getAxes();

    protected abstract boolean isContinuous();

    protected void addMoves(Collection<ChessMove> moves, ChessPosition start, ChessPosition end) {
        moves.add(new ChessMove(start, end));
    }

    protected IntTuple getEndOffset(Boolean... flips) {
        IntTuple endOffset = startModifier().apply(new IntTuple(1));
        for (int i = 0; i < flips.length; i++) {
            if (flips[i]) {
                endOffset = getAxes()[i].apply(endOffset);
            }
        }
        return endOffset;
    }

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition start) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor currentTeam = board.getPiece(start).getTeamColor();

        for (var perm : new BooleanCombinations(getAxes().length)) {
            Boolean[] offsets = asArray(perm.values());
            IntTuple endOffset = getEndOffset(offsets);
            int i = 0;
            do {
                i++;
                ChessPosition end = endOffset.mul(i).newPosition(start);
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
