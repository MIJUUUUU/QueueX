package ui;

import dto.Admin;
import service.AdminService;

import java.util.Scanner;

public class AdminUI {
  private Scanner s = new Scanner(System.in);
  private AdminService adminService = new AdminService();

  public void adminStart() {
    Admin admin = login();

    if (admin == null) {
      return;
    }
    AdminMenuUI adminMenuUI = new AdminMenuUI();
    adminMenuUI.showAdminMenu(admin);
  }

  private Admin login() {
    int failCount = 0;

    while (failCount < 3) {
      System.out.print("관리자 인증번호를 입력해주세요 >> ");
      String inputCode = s.nextLine().trim();

      Admin admin = adminService.adminLogin(inputCode);

      if (admin != null) {
        System.out.println("로그인에 성공했습니다.");
        return admin;
      }

      failCount++;

      if (failCount == 3) {
        System.out.println("인증에 3회 실패하였습니다. 프로그램이 종료됩니다.");
        return null;
      }

      System.out.println("인증번호가 올바르지 않습니다.");
      System.out.println("현재 실패 횟수: " + failCount + "회");
      System.out.println("인증 3회 실패시 프로그램이 종료됩니다.");
    }

    return null;
  }

}
