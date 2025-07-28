package ui;

/**
 * This class contains constants and functions relating to ANSI Escape Sequences that are useful in the Client display
 */
@SuppressWarnings("unused")
public final class EscapeSequences {

    private static final String UNICODE_ESCAPE = "\u001b";
    private static final String ANSI_ESCAPE = "\033";

    private static final String CONTROL_SEQUENCE = UNICODE_ESCAPE + "[";

    public static final String RESET_FORMATTING = CONTROL_SEQUENCE + "0m";

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
    //endregion

    //region Text Color
    private static final String SET_TEXT_COLOR = CONTROL_SEQUENCE + "38;5;";

    public static final String SET_TEXT_COLOR_BLACK = SET_TEXT_COLOR + "0m";
    public static final String SET_TEXT_COLOR_LIGHT_GREY = SET_TEXT_COLOR + "242m";
    public static final String SET_TEXT_COLOR_DARK_GREY = SET_TEXT_COLOR + "235m";
    public static final String SET_TEXT_COLOR_RED = SET_TEXT_COLOR + "160m";
    public static final String SET_TEXT_COLOR_GREEN = SET_TEXT_COLOR + "46m";
    public static final String SET_TEXT_COLOR_DARK_GREEN = SET_TEXT_COLOR + "22m";
    public static final String SET_TEXT_COLOR_YELLOW = SET_TEXT_COLOR + "226m";
    public static final String SET_TEXT_COLOR_BLUE = SET_TEXT_COLOR + "12m";
    public static final String SET_TEXT_COLOR_MAGENTA = SET_TEXT_COLOR + "5m";
    public static final String SET_TEXT_COLOR_WHITE = SET_TEXT_COLOR + "15m";
    public static final String RESET_TEXT_COLOR = CONTROL_SEQUENCE + "39m";
    //endregion

    //region BG Color
    private static final String SET_BG_COLOR = CONTROL_SEQUENCE + "48;5;";

    public static final String SET_BG_COLOR_BLACK = SET_BG_COLOR + "0m";
    public static final String SET_BG_COLOR_LIGHT_GREY = SET_BG_COLOR + "242m";
    public static final String SET_BG_COLOR_DARK_GREY = SET_BG_COLOR + "235m";
    public static final String SET_BG_COLOR_RED = SET_BG_COLOR + "160m";
    public static final String SET_BG_COLOR_GREEN = SET_BG_COLOR + "46m";
    public static final String SET_BG_COLOR_DARK_GREEN = SET_BG_COLOR + "22m";
    public static final String SET_BG_COLOR_YELLOW = SET_BG_COLOR + "226m";
    public static final String SET_BG_COLOR_BLUE = SET_BG_COLOR + "12m";
    public static final String SET_BG_COLOR_MAGENTA = SET_BG_COLOR + "5m";
    public static final String SET_BG_COLOR_WHITE = SET_BG_COLOR + "15m";
    public static final String RESET_BG_COLOR = CONTROL_SEQUENCE + "49m";
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
