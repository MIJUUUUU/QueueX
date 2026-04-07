package common;

public class ValidationUtil {

    private ValidationUtil() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean hasText(String value) {
        return !isBlank(value);
    }

    public static boolean isInteger(String value) {
        if (isBlank(value)) {
            return false;
        }

        try {
            Integer.parseInt(value.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isPositiveInteger(String value) {
        if (!isInteger(value)) {
            return false;
        }

        return Integer.parseInt(value.trim()) > 0;
    }

    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }
}
