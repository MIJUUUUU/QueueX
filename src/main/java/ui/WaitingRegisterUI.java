package ui;

import common.ValidationUtil;
import dto.Customer;
import dto.Menu;
import dto.Store;
import dto.Waiting;
import service.WaitingService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class WaitingRegisterUI {

    private final Scanner scanner;
    private final WaitingService waitingService = new WaitingService();

    public WaitingRegisterUI(Scanner scanner) {
        this.scanner = scanner;
    }

    public void handle(Customer customer) {
        // 1. 가게 목록 출력
        List<Store> stores = waitingService.getAllStores();
        if (stores.isEmpty()) {
            System.out.println("등록된 가게가 없습니다.");
            return;
        }

        System.out.println("\n=== 가게 목록 ===");
        for (int i = 0; i < stores.size(); i++) {
            Store s = stores.get(i);
            System.out.println((i + 1) + ". " + s.getStoreName() + " [" + s.getCategory() + "]");
        }
        System.out.println("0. 취소");
        System.out.print("가게를 선택하세요: ");
        String storeInput = scanner.nextLine().trim();

        if ("0".equals(storeInput)) {
            return;
        }

        if (!ValidationUtil.isPositiveInteger(storeInput)) {
            System.out.println("올바른 번호를 입력하세요.");
            return;
        }

        int selectedStoreNumber = Integer.parseInt(storeInput);
        if (!ValidationUtil.isInRange(selectedStoreNumber, 1, stores.size())) {
            System.out.println("올바른 번호를 입력하세요.");
            return;
        }

        int storeIndex = selectedStoreNumber - 1;
        Store selectedStore = stores.get(storeIndex);

        // 2. 메뉴 목록 출력 및 선주문 선택
        List<Menu> menus = waitingService.getMenusByStoreId(selectedStore.getStoreId());
        Map<Integer, Integer> selectedMenus = new LinkedHashMap<>();

        if (!menus.isEmpty()) {
            System.out.println("\n=== " + selectedStore.getStoreName() + " 메뉴 ===");
            for (int i = 0; i < menus.size(); i++) {
                Menu m = menus.get(i);
                System.out.println((i + 1) + ". " + m.getMenuName() + " - " + m.getPrice() + "원");
            }
            System.out.println("메뉴 번호와 수량을 입력하세요 (예: 1 2 → 1번 메뉴 2개, 완료 시 0 입력)");

            while (true) {
                System.out.print("입력: ");
                String line = scanner.nextLine().trim();
                if (line.equals("0")) {
                    if (selectedMenus.isEmpty()) {
                        System.out.println("메뉴를 1개 이상 선택해야 합니다.");
                        continue;
                    }
                    break;
                }

                String[] parts = line.split("\\s+");
                if (parts.length != 2) {
                    System.out.println("형식이 올바르지 않습니다. 예) 1 2");
                    continue;
                }

                if (!ValidationUtil.isPositiveInteger(parts[0]) || !ValidationUtil.isPositiveInteger(parts[1])) {
                    System.out.println("올바른 번호를 입력하세요.");
                    continue;
                }

                int menuNumber = Integer.parseInt(parts[0]);
                int quantity = Integer.parseInt(parts[1]);

                if (!ValidationUtil.isInRange(menuNumber, 1, menus.size())) {
                    System.out.println("올바른 번호를 입력하세요.");
                    continue;
                }

                int menuIndex = menuNumber - 1;
                Menu selected = menus.get(menuIndex);
                selectedMenus.put(selected.getMenuId(), quantity);
                System.out.println(selected.getMenuName() + " " + quantity + "개 추가됨.");
            }
        }

        // 3. 인원수 입력
        System.out.print("\n인원수를 입력하세요: ");
        String peopleInput = scanner.nextLine().trim();
        if (!ValidationUtil.isPositiveInteger(peopleInput)) {
            System.out.println("올바른 인원수를 입력하세요.");
            return;
        }

        int peopleCount = Integer.parseInt(peopleInput);

        // 4. 대기 등록
        Waiting waiting = waitingService.registerWaiting(
            customer.getCustomerId(),
            selectedStore.getStoreId(),
            peopleCount,
            selectedMenus
        );

        if (waiting != null) {
            System.out.println("\n대기 등록이 완료되었습니다.");
            System.out.println("가게: " + selectedStore.getStoreName());
            System.out.println("대기 번호: " + waiting.getWaitingNumber());
            System.out.println("인원수: " + waiting.getPeopleCount() + "명");
            System.out.println("주문내역:");

            if (selectedMenus.isEmpty()) {
                System.out.println("- 없음");
            } else {
                for (Map.Entry<Integer, Integer> entry : selectedMenus.entrySet()) {
                    Menu orderedMenu = null;

                    for (Menu menu : menus) {
                        if (menu.getMenuId() == entry.getKey()) {
                            orderedMenu = menu;
                            break;
                        }
                    }

                    String menuName = orderedMenu != null ? orderedMenu.getMenuName() : "알 수 없는 메뉴";
                    System.out.println("- " + menuName + " " + entry.getValue() + "개");
                }
            }
        } else {
            System.out.println("대기 등록에 실패했습니다. 다시 시도해주세요.");
        }
    }
}
