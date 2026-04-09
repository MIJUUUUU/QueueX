package common;

public final class ConsoleStyle {

    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String BLUE = "\u001B[34m";

    private ConsoleStyle() {
    }

    public static String divider() {
        return color("========================================", BLUE);
    }

    public static String title(String text) {
        String safeText = text == null ? "" : text;
        int leftPadding = Math.max(0, (40 - displayWidth(safeText)) / 2);
        return BOLD + CYAN + " ".repeat(leftPadding) + safeText + RESET;
    }

    public static String centeredTitle(String text, int width) {
        String safeText = text == null ? "" : text;
        int textWidth = displayWidth(safeText);
        int leftPadding = Math.max(0, (width - textWidth) / 2);
        return title(" ".repeat(leftPadding) + safeText);
    }

    public static String success(String text) {
        return color(text, GREEN);
    }

    public static String warning(String text) {
        return color(text, YELLOW);
    }

    public static String error(String text) {
        return color(text, RED);
    }

    public static String info(String text) {
        return color(text, CYAN);
    }

    public static String highlight(String text) {
        return BOLD + text + RESET;
    }

    public static String color(String text, String color) {
        return color + text + RESET;
    }

    public static String padRight(String text, int width) {
        String safeText = text == null ? "" : text;
        int padding = Math.max(0, width - displayWidth(safeText));
        return safeText + " ".repeat(padding);
    }

    public static int displayWidth(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        int width = 0;
        for (char ch : text.toCharArray()) {
            width += isWideCharacter(ch) ? 2 : 1;
        }
        return width;
    }

    private static boolean isWideCharacter(char ch) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(ch);
        return block == Character.UnicodeBlock.HANGUL_SYLLABLES
            || block == Character.UnicodeBlock.HANGUL_JAMO
            || block == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO
            || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
            || block == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
            || block == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
            || block == Character.UnicodeBlock.HIRAGANA
            || block == Character.UnicodeBlock.KATAKANA
            || block == Character.UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS;
    }
}
