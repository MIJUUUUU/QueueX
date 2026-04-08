package ui;

import common.ValidationUtil;
import dto.Admin;
import dto.Store;
import service.AdminService;

import java.util.List;
import java.util.Scanner;

public class AdminStoreUI {
  private final Scanner s;
  private final AdminService adminService = new AdminService();
  private final AdminSeatUI adminSeatUI;

  public AdminStoreUI(Scanner scanner) {
    this.s = scanner;
    this.adminSeatUI = new AdminSeatUI(scanner);
  }

  public void manageStore(Admin admin) { // 관리자 가게 목록 출력 및 선택
    List<Store> stores = adminService.getStoresByAdmin(admin);

    if (stores.isEmpty()) {
      System.out.println("운영 가능한 가게가 없습니다.");
      return;
    }

    while (true) {
      System.out.println("[선택 가능한 가게 목록]");

      for (int i = 0; i < stores.size(); i++) {
        Store store = stores.get(i);
        System.out.println((i + 1) + ". " + store.getStoreName());
      }

      System.out.println("0. 뒤로 가기");
      System.out.print("선택 >> ");
      String input = s.nextLine().trim();

      if ("0".equals(input)) {
        return;
      }

      if (!ValidationUtil.isPositiveInteger(input)) {
        System.out.println("잘못된 입력입니다. 숫자를 입력해주세요.");
        continue;
      }

      int selected = Integer.parseInt(input);

      if (selected < 1 || selected > stores.size()) {
        System.out.println("목록에 있는 번호를 입력해주세요.");
        continue;
      }

      // 선택한 가게 운영 화면으로 진입.
      Store selectedStore = stores.get(selected - 1);
      System.out.println(selectedStore.getStoreName() + " 관리자 메뉴로 진입합니다.");
      adminSeatUI.startSeatFlow(selectedStore);
      return;
    }
  }
}
