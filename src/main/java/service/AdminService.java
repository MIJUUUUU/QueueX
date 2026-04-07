package service;

import common.PasswordUtil;
import common.ValidationUtil;
import dao.AdminDAO;
import dto.Admin;

import java.util.List;

public class AdminService {
  private final AdminDAO adminDAO = new AdminDAO();

  public Admin adminLogin(String rawAdminCode) {
    if (ValidationUtil.isBlank(rawAdminCode)) {
      return null;
    }

    List<Admin> admins = adminDAO.findAllAdmins();

    for (Admin admin : admins) {
      if (PasswordUtil.matches(rawAdminCode.trim(), admin.getAdminAuthCode())) {
        return admin;
      }
    }

    return null;
  }
}