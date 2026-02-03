package chess.calculator;

import chess.PieceMoveCalculator;
import utils.EnumObjectFactory;

import static chess.ChessPiece.PieceType;

public final class PieceMoveCalculatorFactory extends EnumObjectFactory<PieceType, PieceMoveCalculator> {
    public PieceMoveCalculatorFactory() {
        super(true);
    }

    @Override
    protected Class<PieceType> getKeyClass() {
        return PieceType.class;
    }

    @Override
    protected PieceMoveCalculator generateValue(PieceType key) {
        return switch (key) {
            case BISHOP -> new BishopMoveCalculator();
            case ROOK   -> new RookMoveCalculator();
            case QUEEN  -> new QueenMoveCalculator();
            case KING   -> new KingMoveCalculator();
            case KNIGHT -> new KnightMoveCalculator();
            case PAWN   -> new PawnMoveCalculator();
        };
    }
}
