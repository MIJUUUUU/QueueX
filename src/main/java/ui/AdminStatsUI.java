package ui;

import common.ValidationUtil;
import dto.Admin;
import dto.AdminStatistics;
import dto.MenuStat;
import dto.Store;
import service.AdminService;

import java.util.List;
import java.util.Scanner;

public class AdminStatsUI {
  private final Scanner s;
  private final AdminService adminService = new AdminService();

  public AdminStatsUI(Scanner scanner) {
    this.s = scanner;
  }

  public void showStatistics(Admin admin) {
    List<Store> stores = adminService.getStoresByAdmin(admin);

    if (stores.isEmpty()) {
      System.out.println("조회 가능한 가게가 없습니다.");
      return;
    }

    while (true) {
      System.out.println("===== 가게 목록 =====");

      for (int i = 0; i < stores.size(); i++) {
        System.out.println((i + 1) + ". " + stores.get(i).getStoreName());
      }

      System.out.print("선택 >> ");
      String input = s.nextLine().trim();

      if (!ValidationUtil.isPositiveInteger(input)) {
        System.out.println("잘못된 입력입니다. 숫자를 입력해주세요.");
        continue;
      }

      int selected = Integer.parseInt(input);
      if (selected < 1 || selected > stores.size()) {
        System.out.println("목록에 있는 번호를 입력해주세요.");
        continue;
      }

      printStoreStatistics(stores.get(selected - 1));
      return;
    }
  }

  private void printStoreStatistics(Store store) {
    AdminStatistics statistics = adminService.getTodayStatistics(store);

    while (true) {
      System.out.println();
      System.out.println("[" + store.getStoreName() + " - 통계 조회]");
      System.out.println();
      System.out.println("===== 오늘 통계 =====");
      System.out.println("총 방문 고객 수: " + statistics.getTotalVisitedCustomers() + "명");
      System.out.println("총 웨이팅 수: " + statistics.getTotalWaitingCount() + "건");
      System.out.println();
      System.out.println("===== 메뉴 통계 =====");
      if (statistics.getMenuStats().isEmpty()) {
        System.out.println("주문 내역이 없습니다.");
      } else {
        for (MenuStat menuStat : statistics.getMenuStats()) {
          System.out.println(menuStat.getMenuName() + ": " + menuStat.getOrderCount() + "건");
        }
      }
      System.out.println();
      System.out.println("===== 노쇼 =====");
      System.out.println("노쇼 건수: " + statistics.getNoShowCount() + "건");
      System.out.println();
      System.out.println("0. 뒤로가기");
      System.out.print("선택 >> ");

      String input = s.nextLine().trim();
      if ("0".equals(input)) {
        return;
      }

      System.out.println("잘못된 입력입니다. 뒤로가려면 0을 입력해주세요.");
    }
  }
}
