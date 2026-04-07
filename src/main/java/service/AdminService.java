package service;

import common.PasswordUtil;
import dao.AdminDAO;
import dto.Admin;
import dto.Store;

import java.util.Collections;
import java.util.List;

public class AdminService {
  AdminDAO adminDAO = new AdminDAO();

  public Admin adminLogin(String adminAuthCode) {
    if (adminAuthCode == null || adminAuthCode.trim().isEmpty()) {
      return null;
    }

    List<Admin> admins = adminDAO.findAllAdmins();

    for (Admin admin : admins) {
      if (PasswordUtil.matches(adminAuthCode.trim(), admin.getAdminAuthCode())) {
        return admin;
      }
    }

    return null;
  }

  public List<Store> getStoresByAdmin(Admin admin) {
    if (admin == null) {
      return Collections.emptyList();
    }

    return adminDAO.findStoresByAdminId(admin.getAdminId());
  }
}