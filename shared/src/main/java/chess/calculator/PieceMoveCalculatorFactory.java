package chess.calculator;

import chess.PieceMoveCalculator;
import utils.EnumObjectFactory;

import static chess.ChessPiece.PieceType;

public final class PieceMoveCalculatorFactory extends EnumObjectFactory<PieceType, PieceMoveCalculator> {
    private PieceMoveCalculatorFactory() {
        super(true);
    }

    @Override
    protected Class<PieceType> getKeyClass() {
        return PieceType.class;
    }

    @Override
    protected PieceMoveCalculator preGenerateValue(PieceType key) {
        return switch (key) {
            case BISHOP -> new DiagonalMoveCalculator();
            case ROOK -> new CrossMoveCalculator();
            case QUEEN -> new QueenMoveCalculator();
            case KING -> new KingMoveCalculator();
            case KNIGHT -> new KnightMoveCalculator();
            case PAWN -> new PawnMoveCalculator();
        };
    }

    public static PieceMoveCalculator getFrom(PieceType type) {
        return INSTANCE.get(type);
    }

    private static final PieceMoveCalculatorFactory INSTANCE = new PieceMoveCalculatorFactory();
}
