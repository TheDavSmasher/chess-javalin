package ui;

import chess.ChessGame;
import chess.ChessPiece;

import java.util.function.Function;

/**
 * This class contains constants and functions relating to ANSI Escape Sequences that are useful in the Client display
 */
@SuppressWarnings("unused")
public final class EscapeSequences {

    private static final String UNICODE_ESCAPE = "\u001b";
    private static final String ANSI_ESCAPE = "\033";

    private static final String CONTROL_SEQUENCE = UNICODE_ESCAPE + "[";

    public static final String ERASE_SCREEN = CONTROL_SEQUENCE + "H" + CONTROL_SEQUENCE + "2J";
    public static final String ERASE_LINE = CONTROL_SEQUENCE + "2K";

    //region Text Format
    public interface SGR {
        int param();
        int[] EMPTY = {};
        default int[] extraParams(Format<?> format) {
            return EMPTY;
        }
    }

    public record Format<T extends SGR>(int formatMod, Function<T, Integer> resetMod) {
        public static final Format<Style> STYLE = new Format<>(2, Style::param);
        public static final Format<Color> TEXT = new Format<>(3, Color::reset);
        public static final Format<Color> BG = new Format<>(4, Color::reset);

        public String set(T sgr) {
            return setSequence(sgr.param(), sgr.extraParams(this));
        }

        public String reset(T sgr) {
            return setSequence(formatMod * 10 + resetMod.apply(sgr));
        }

        public String reset() {
            return reset(null);
        }

        public static String resetAll() {
            return setSequence(0);
        }

        private static String setSequence(int param, int... params) {
            StringBuilder result = new StringBuilder().append(CONTROL_SEQUENCE);
            for (int p : params) {
                result.append(p).append(";");
            }
            return result.append(param).append("m").toString();
        }
    }

    public enum Style implements SGR {
        BOLD,
        FAINT,
        ITALIC,
        UNDERLINE,
        BLINKING;

        public int param() {
            return ordinal() + 1;
        }
    }

    public record Color(int param) implements SGR {
        public static final Color BLACK = new Color(0);
        public static final Color LIGHT_GREY = new Color(242);
        public static final Color DARK_GREY = new Color(235);
        public static final Color RED = new Color(160);
        public static final Color GREEN = new Color(46);
        public static final Color DARK_GREEN = new Color(22);
        public static final Color YELLOW = new Color(226);
        public static final Color BLUE = new Color(12);
        public static final Color MAGENTA = new Color(5);
        public static final Color WHITE = new Color(15);

        public static int reset(Color color) {
            return 9;
        }

        public int[] extraParams(Format<?> format) {
            return new int[] { format.formatMod() * 10 + 8, 5 };
        }
    }
    //endregion

    //region Pieces
    public static String getAsUnicode(ChessPiece piece) {
        return getAsUnicode(piece.type(), piece.getTeamColor() == ChessGame.TeamColor.WHITE);
    }

    public static String getAsUnicode(ChessPiece.PieceType type, boolean isWhite) {
        return " " + ('♔' + type.ordinal() + (isWhite ? 0 : 6)) + " ";
    }

    public static final String EMPTY = " \u2003 ";
    //endregion

    public static String moveCursorToLocation(int x, int y) { return CONTROL_SEQUENCE + y + ";" + x + "H"; }
}
