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

    showAdminMenu();
  }

  private Admin login() {
    int failCount = 0;

    while (failCount < 3) {
      System.out.println("관리자 id를 입력해주세요 >> ");
      String input = s.nextLine();

      Admin admin = adminService.adminLogin(input);

      if (admin != null) {
        System.out.println("로그인에 성공했습니다.");
        return admin;
      }

      failCount++;

      if (failCount == 3) {
        System.out.println("인증에 3회 실패하였습니다. 프로그램이 종료됩니다.");
        return null;
      }

      System.out.println("id를 " + failCount + "회 틀렸습니다.");
      System.out.println("3회 실패시 프로그램이 종료됩니다.");

    }

    return null;
  }

  private void showAdminMenu() {
    while (true) {
      System.out.println("""
          [관리자 메뉴]
          1. 가게 선택 및 운영
          2  통계 조회
          0. 시스템 종료
          -------------------------
          선택 >>
          """);

      String input = s.nextLine();

      switch (input) {
        case "1":
          System.out.println("가게 선택 및 운영을 선택하셨습니다.");
          break;
        case "2":
          System.out.println("통계 조회를 선택하셨습니다.");
          break;
        case "0":
          System.out.println("시스템이 종료됩니다.");
          return;
        default:
          System.out.println("잘못된 입력입니다. 다시 선택해주세요");
      }
    }
  }
}
