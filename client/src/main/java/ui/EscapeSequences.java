package ui;

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
    public static final String SET_TEXT_BOLD = CONTROL_SEQUENCE + "1m";
    public static final String SET_TEXT_FAINT = CONTROL_SEQUENCE + "2m";
    public static final String RESET_TEXT_BOLD_FAINT = CONTROL_SEQUENCE + "22m";
    public static final String SET_TEXT_ITALIC = CONTROL_SEQUENCE + "3m";
    public static final String RESET_TEXT_ITALIC = CONTROL_SEQUENCE + "23m";
    public static final String SET_TEXT_UNDERLINE = CONTROL_SEQUENCE + "4m";
    public static final String RESET_TEXT_UNDERLINE = CONTROL_SEQUENCE + "24m";
    public static final String SET_TEXT_BLINKING = CONTROL_SEQUENCE + "5m";
    public static final String RESET_TEXT_BLINKING = CONTROL_SEQUENCE + "25m";

    public static final String RESET_FORMATTING = CONTROL_SEQUENCE + "0m";

    //endregion

    //region Text Color
    public enum Color {
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

        public String setText() {
            return getSGR(false);
        }

        public String setBG() {
            return getSGR(true);
        }

        String getSGR(boolean isBG) {
            return CONTROL_SEQUENCE + (isBG ? "4" : "3") + "8;5;" + sgrParam + "m";
        }

        public static String reset(boolean isBG) {
            return CONTROL_SEQUENCE + (isBG ? "4" : "3") + "9m";
        }
    }

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
