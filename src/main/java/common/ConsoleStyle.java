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
        return BOLD + CYAN + text + RESET;
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
}
