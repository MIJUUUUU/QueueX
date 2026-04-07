package common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {

  private PasswordUtil() {
  }

  public static String hashPassword(String rawValue) {
    if (rawValue == null || rawValue.trim().isEmpty()) {
      throw new IllegalArgumentException("입력값은 비어 있을 수 없습니다.");
    }

    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] hashedBytes = md.digest(rawValue.getBytes(StandardCharsets.UTF_8));

      StringBuilder sb = new StringBuilder();
      for (byte b : hashedBytes) {
        sb.append(String.format("%02x", b));
      }
      return sb.toString();

    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 알고리즘을 사용할 수 없습니다.", e);
    }
  }

  public static boolean matches(String rawValue, String hashedValue) {
    if (rawValue == null || hashedValue == null) {
      return false;
    }

    return hashPassword(rawValue).equals(hashedValue);
  }
}