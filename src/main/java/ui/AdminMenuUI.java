package ui;

import common.ConsoleStyle;
import dto.Admin;

import java.util.Scanner;

public class AdminMenuUI {
  private final Scanner s;
  private final AdminStoreUI adminStoreUI;
  private final AdminStatsUI adminStatsUI;

  public AdminMenuUI(Scanner scanner) {
    this.s = scanner;
    this.adminStoreUI = new AdminStoreUI(scanner);
    this.adminStatsUI = new AdminStatsUI(scanner);
  }

  public void showAdminMenu(Admin admin) {
    while (true) {
      System.out.println();
      System.out.println(ConsoleStyle.divider());
      System.out.println(ConsoleStyle.title("관리자 메뉴"));
      System.out.println(ConsoleStyle.divider());
      System.out.println("1. 가게 선택 및 운영");
      System.out.println("2. 통계 조회");
      System.out.println("0. 로그아웃");
      System.out.println(ConsoleStyle.divider());
      System.out.print("선택 >> ");

      String input = s.nextLine().trim();

      switch (input) {
        case "1":
          clearConsole();
          System.out.println(ConsoleStyle.info("가게 선택 및 운영 화면으로 이동합니다."));
          adminStoreUI.manageStore(admin);
          break;
        case "2":
          clearConsole();
          System.out.println(ConsoleStyle.info("통계 조회 화면으로 이동합니다."));
          adminStatsUI.showStatistics(admin);
          break;
        case "0":
          System.out.println(ConsoleStyle.info("로그아웃합니다."));
          return;
        default:
          System.out.println(ConsoleStyle.error("잘못된 입력입니다. 다시 입력해주세요."));
      }
    }
  }

  private void clearConsole() {
    System.out.print("\033[2J\033[3J\033[H");
    System.out.flush();
  }
}
