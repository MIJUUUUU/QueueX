package ui;

import common.ConsoleStyle;
import dto.Admin;
import service.AdminService;

import java.util.Scanner;

public class AdminUI {
  private final Scanner s;
  private final AdminService adminService = new AdminService();
  private final AdminMenuUI adminMenuUI;

  public AdminUI(Scanner scanner) {
    this.s = scanner;
    this.adminMenuUI = new AdminMenuUI(scanner);
  }

  public void adminStart() {
    Admin admin = login();

    if (admin == null) {
      return;
    }
    adminMenuUI.showAdminMenu(admin);
  }

  private Admin login() {
    int failCount = 0;

    System.out.println();
    System.out.println(ConsoleStyle.divider());
    System.out.println(ConsoleStyle.title("관리자 인증"));
    System.out.println(ConsoleStyle.divider());

    while (failCount < 3) {
      System.out.print("관리자 인증번호를 입력해주세요 >> ");
      String inputCode = s.nextLine().trim();

      Admin admin = adminService.adminLogin(inputCode);

      if (admin != null) {
        System.out.println();
        System.out.println(ConsoleStyle.success("관리자 로그인에 성공했습니다."));
        return admin;
      }

      failCount++;

      if (failCount == 3) {
        System.out.println();
        System.out.println(ConsoleStyle.error("인증에 3회 실패하였습니다. 프로그램을 종료합니다."));
        return null;
      }

      System.out.println();
      System.out.println(ConsoleStyle.error("인증번호가 올바르지 않습니다."));
      System.out.println(ConsoleStyle.warning("현재 실패 횟수: " + failCount + "회"));
      System.out.println(ConsoleStyle.warning("3회 실패 시 프로그램이 종료됩니다."));
      System.out.println(ConsoleStyle.divider());
    }

    return null;
  }

}
