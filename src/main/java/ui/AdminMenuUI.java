package ui;

import dto.Admin;

import java.util.Scanner;

public class AdminMenuUI {
  private final Scanner s = new Scanner(System.in);

  public void showAdminMenu(Admin admin) {
    while (true) {
      System.out.println("""
                [관리자 메뉴]

                1. 가게 선택 및 운영 시작
                2. 통계 조회
                0. 로그아웃

                선택 >>
                """);

      String input = s.nextLine().trim();

      switch (input) {
        case "1":
          System.out.println("가게 선택 및 운영 시작을 선택하셨습니다.");
          adminStoreUI.managerStore(admin);
          break;
        case "2":
          System.out.println("통계 조회를 선택하셨습니다.");
          break;
        case "0":
          System.out.println("로그아웃합니다.");
          return;
        default:
          System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
      }
    }
  }
}