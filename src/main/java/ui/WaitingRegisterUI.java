package ui;

import common.ConsoleStyle;
import common.ValidationUtil;
import dto.Customer;
import dto.Menu;
import dto.Store;
import dto.Waiting;
import service.WaitingService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class WaitingRegisterUI {

    private final Scanner scanner;
    private final WaitingService waitingService = new WaitingService();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public WaitingRegisterUI(Scanner scanner) {
        this.scanner = scanner;
    }

    public void handle(Customer customer) {
        while (true) {
            List<Store> stores = waitingService.getAllStores();
            if (stores.isEmpty()) {
                System.out.println(ConsoleStyle.warning("등록된 가게가 없습니다."));
                return;
            }

            System.out.println();
            System.out.println(ConsoleStyle.divider());
            System.out.println(ConsoleStyle.title("가게 목록"));
            System.out.println(ConsoleStyle.divider());
            System.out.printf("%-4s %-16s %-10s%n", "번호", "가게명", "카테고리");
            System.out.println("----------------------------------------");
            for (int i = 0; i < stores.size(); i++) {
                Store s = stores.get(i);
                System.out.printf("%-4d %-16s %-10s%n", i + 1, s.getStoreName(), "[" + s.getCategory() + "]");
            }
            System.out.println("----------------------------------------");
            System.out.println("0. 취소");
            System.out.print("가게를 선택하세요: ");
            String storeInput = scanner.nextLine().trim();

            if ("0".equals(storeInput)) {
                return;
            }

            if (!ValidationUtil.isPositiveInteger(storeInput)) {
                System.out.println(ConsoleStyle.error("올바른 번호를 입력하세요."));
                continue;
            }

            int selectedStoreNumber = Integer.parseInt(storeInput);
            if (!ValidationUtil.isInRange(selectedStoreNumber, 1, stores.size())) {
                System.out.println(ConsoleStyle.error("올바른 번호를 입력하세요."));
                continue;
            }

            int storeIndex = selectedStoreNumber - 1;
            Store selectedStore = stores.get(storeIndex);

            if (waitingService.hasActiveWaitingAtStore(customer.getCustomerId(), selectedStore.getStoreId())) {
                System.out.println(ConsoleStyle.warning(selectedStore.getStoreName() + "에는 이미 진행 중인 대기가 있습니다."));
                System.out.println(ConsoleStyle.info("현재 대기 현황에서 확인하거나 취소 후 다시 등록해주세요."));
                continue;
            }

            List<Menu> menus = waitingService.getMenusByStoreId(selectedStore.getStoreId());
            Map<Integer, Integer> selectedMenus = new LinkedHashMap<>();
            boolean backToStoreSelection = false;

            if (!menus.isEmpty()) {
                System.out.println();
                System.out.println(ConsoleStyle.divider());
                System.out.println(ConsoleStyle.title(selectedStore.getStoreName() + " 메뉴"));
                System.out.println(ConsoleStyle.divider());
                System.out.printf("%-4s %-16s %-10s%n", "번호", "메뉴명", "가격");
                System.out.println("----------------------------------------");
                for (int i = 0; i < menus.size(); i++) {
                    Menu m = menus.get(i);
                    System.out.printf("%-4d %-16s %-10s%n", i + 1, m.getMenuName(), m.getPrice() + "원");
                }
                System.out.println("----------------------------------------");
                System.out.println();
                System.out.println("┌──────────────────────────────────────┐");
                System.out.println("│ 주문 방법                            │");
                System.out.println("│ - 메뉴 번호와 수량을 입력하세요      │");
                System.out.println("│   예: 1 2  ->  1번 메뉴 2개          │");
                System.out.println("│ - 주문 완료: 0                       │");
                System.out.println("│ - 가게 목록으로 돌아가기: B          │");
                System.out.println("└──────────────────────────────────────┘");

                while (true) {
                    System.out.print("입력: ");
                    String line = scanner.nextLine().trim();

                    if ("B".equalsIgnoreCase(line)) {
                        backToStoreSelection = true;
                        break;
                    }

                    if (line.equals("0")) {
                        if (selectedMenus.isEmpty()) {
                            System.out.println(ConsoleStyle.warning("메뉴를 1개 이상 선택해야 합니다. 가게 선택으로 돌아가려면 B를 입력하세요."));
                            continue;
                        }
                        break;
                    }

                    String[] parts = line.split("\\s+");
                    if (parts.length != 2) {
                        System.out.println(ConsoleStyle.error("형식이 올바르지 않습니다. 예) 1 2"));
                        continue;
                    }

                    if (!ValidationUtil.isPositiveInteger(parts[0]) || !ValidationUtil.isPositiveInteger(parts[1])) {
                        System.out.println(ConsoleStyle.error("올바른 번호를 입력하세요."));
                        continue;
                    }

                    int menuNumber = Integer.parseInt(parts[0]);
                    int quantity = Integer.parseInt(parts[1]);

                    if (!ValidationUtil.isInRange(menuNumber, 1, menus.size())) {
                        System.out.println(ConsoleStyle.error("올바른 번호를 입력하세요."));
                        continue;
                    }

                    int menuIndex = menuNumber - 1;
                    Menu selected = menus.get(menuIndex);
                    selectedMenus.put(selected.getMenuId(), quantity);
                    System.out.println(ConsoleStyle.success(selected.getMenuName() + " " + quantity + "개 추가됨."));
                }
            }

            if (backToStoreSelection) {
                continue;
            }

            int peopleCount = 0;
            while (true) {
                System.out.print("\n인원수를 입력하세요 (가게 선택으로 돌아가려면 0 입력): ");
                String peopleInput = scanner.nextLine().trim();

                if ("0".equals(peopleInput)) {
                    backToStoreSelection = true;
                    break;
                }

                if (!ValidationUtil.isPositiveInteger(peopleInput)) {
                    System.out.println(ConsoleStyle.error("올바른 인원수를 입력하세요."));
                    continue;
                }

                peopleCount = Integer.parseInt(peopleInput);
                break;
            }

            if (backToStoreSelection) {
                continue;
            }

            Waiting waiting = waitingService.registerWaiting(
                customer.getCustomerId(),
                selectedStore.getStoreId(),
                peopleCount,
                selectedMenus
            );

            if (waiting != null) {
                System.out.println();
                System.out.println(ConsoleStyle.divider());
                System.out.println(ConsoleStyle.success("대기 등록이 완료되었습니다."));
                System.out.println(ConsoleStyle.divider());
                System.out.println("가게      : " + selectedStore.getStoreName());
                System.out.println("대기 번호 : " + waiting.getWaitingNumber());
                System.out.println("인원수    : " + waiting.getPeopleCount() + "명");
                System.out.println("등록 시각 : " + formatDateTime(waiting.getCreatedAt()));
                System.out.println("주문내역  :");

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
                System.out.println(ConsoleStyle.error("대기 등록에 실패했습니다. 다시 시도해주세요."));
            }
            return;
        }
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "-";
        }
        return dateTime.format(DATE_TIME_FORMATTER);
    }
}
