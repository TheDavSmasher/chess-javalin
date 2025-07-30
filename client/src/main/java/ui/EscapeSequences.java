package ui;

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

    public interface SGR {
        int param();
        int[] EMPTY = {};
        default int[] extraParams(Format<?> format) {
            return EMPTY;
        }
    }

    public static class Format<T extends SGR> {
        public static final Format<Style> STYLE = new Format<>(2, Style::param);
        public static final Format<Color> TEXT = new Format<>(3, Color::reset);
        public static final Format<Color> BG = new Format<>(4, Color::reset);

        private final int formatMod;
        private final Function<T, Integer> resetMod;

        private Format(int formatMod, Function<T, Integer> resetMod) {
            this.formatMod = formatMod;
            this.resetMod = resetMod;
        }

        public String set(T sgr) {
            return setSequence(sgr.param(), sgr.extraParams(this));
        }

        public String reset(T sgr) {
            return setSequence(formatMod(resetMod.apply(sgr)));
        }

        public String reset() {
            return setSequence(formatMod(resetMod.apply(null)));
        }

        public static String resetAll() {
            return setSequence(0);
        }

        public int formatMod(int sum) {
            return formatMod * 10 + sum;
        }

        static String setSequence(int param, int... params) {
            StringBuilder result = new StringBuilder().append(CONTROL_SEQUENCE);
            for (int p : params) {
                result.append(p).append(";");
            }
            return result.append(param).append("m").toString();
        }
    }

    //region Text Format
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
    //endregion

    //region Text Color
    public enum Color implements SGR {
        BLACK(0),
        LIGHT_GREY(242),
        DARK_GREY(235),
        RED(160),
        GREEN(46),
        DARK_GREEN(22),
        YELLOW(226),
        BLUE(12),
        MAGENTA(5),
        WHITE(15);

        final int sgrParam;

        Color(int sgrParam) {
            this.sgrParam = sgrParam;
        }

        public static int reset(Color color) {
            return 9;
        }

        public int param() {
            return sgrParam;
        }

        public int[] extraParams(Format<?> format) {
            return new int[] { format.formatMod(8), 5 };
        }
    }
    //endregion

    //region Pieces
    public static final String WHITE_KING = " ♔ ";
    public static final String WHITE_QUEEN = " ♕ ";
    public static final String WHITE_BISHOP = " ♗ ";
    public static final String WHITE_KNIGHT = " ♘ ";
    public static final String WHITE_ROOK = " ♖ ";
    public static final String WHITE_PAWN = " ♙ ";
    public static final String BLACK_KING = " ♚ ";
    public static final String BLACK_QUEEN = " ♛ ";
    public static final String BLACK_BISHOP = " ♝ ";
    public static final String BLACK_KNIGHT = " ♞ ";
    public static final String BLACK_ROOK = " ♜ ";
    public static final String BLACK_PAWN = " ♟ ";
    public static final String EMPTY = " \u2003 ";
    //endregion

    public static String moveCursorToLocation(int x, int y) { return CONTROL_SEQUENCE + y + ";" + x + "H"; }
}
