package service;

import dao.AdminDAO;
import dto.Admin;

public class AdminService {
  // 로그인, 호출, 입장, 노쇼, 통계 같은 업무 처리
  AdminDAO adminDAO = new AdminDAO();


  public Admin adminLogin(String adminAuthCode) {
    if (adminAuthCode == null || adminAuthCode.trim().isEmpty()) {
      return null;
    }
    return adminDAO.findAdmin(adminAuthCode.trim());

  }
}
