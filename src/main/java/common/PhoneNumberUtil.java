package common;
/* 
저장용: PhoneNumberUtil.normalize(phone)
출력용: PhoneNumberUtil.format(phone)
검증용: PhoneNumberUtil.isValid(phone)

DB 저장: 01012345678
화면 출력: 010-1234-5678

*/

public class PhoneNumberUtil {

    private PhoneNumberUtil() {
    }

    public static String normalize(String phone) {
        if (phone == null) {
            return null;
        }
        return phone.replaceAll("[^0-9]", "");
    }

    public static boolean isValid(String phone) {
        String normalized = normalize(phone);

        if (normalized == null) {
            return false;
        }

        return normalized.matches("^01[0-9]{8,9}$");
    }

    public static String format(String phone) {
        String normalized = normalize(phone);

        if (!isValid(normalized)) {
            throw new IllegalArgumentException("유효하지 않은 전화번호입니다.");
        }

        if (normalized.length() == 10) {
            return normalized.replaceFirst("(\\d{3})(\\d{3})(\\d{4})", "$1-$2-$3");
        }

        return normalized.replaceFirst("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
    }
}
