package common;

public class ValidationUtil {


/*
사용자 입력값이 유효한지 검사
고객/관리자 UI에서 공통으로 재사용
서비스 로직 들어가기 전에 잘못된 입력을 걸러줌

메서드별 역할:

isBlank(String value)
null이거나 공백만 있는 입력인지 확인

hasText(String value)
실제 내용이 있는 문자열인지 확인

isInteger(String value)
정수로 변환 가능한지 확인

isPositiveInteger(String value)
양의 정수인지 확인

isInRange(int value, int min, int max)
메뉴 번호, 선택 번호가 범위 안인지 확인

*/

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
