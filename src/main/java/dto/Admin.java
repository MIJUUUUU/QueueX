package dto;

// DB 데이터 전달 객체
public class Admin {
  // 객체 속성
  private int adminId;
  private String adminAuthCode;

  // 생성자
  public Admin() {
  }
  public Admin(int adminId, String adminAuthCode) {
    this.adminId = adminId;
    this.adminAuthCode = adminAuthCode;
  }

  // getter setter
  public int getAdminId() {
    return adminId;
  }
  public void setAdminId(int adminId) {
    this.adminId = adminId;
  }
  public String getAdminAuthCode() {
    return adminAuthCode;
  }
  public void setAdminAuthCode(String adminAuthCode) {
    this.adminAuthCode = adminAuthCode;
  }
}


