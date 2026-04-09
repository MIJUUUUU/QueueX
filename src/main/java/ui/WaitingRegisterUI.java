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
            String header = ConsoleStyle.padRight("번호", 6)
                + ConsoleStyle.padRight("가게명", 18)
                + ConsoleStyle.padRight("카테고리", 12)
                + ConsoleStyle.padRight("매장 좌석 수", 16)
                + ConsoleStyle.padRight("한 팀 최대", 14);
            System.out.println(header);
            System.out.println("--------------------------------------------------------------------------");
            for (int i = 0; i < stores.size(); i++) {
                Store s = stores.get(i);
                String row = ConsoleStyle.padRight(String.valueOf(i + 1), 6)
                    + ConsoleStyle.padRight(s.getStoreName(), 18)
                    + ConsoleStyle.padRight("[" + s.getCategory() + "]", 12)
                    + ConsoleStyle.padRight(s.getMaxCapacity() + "석", 16)
                    + ConsoleStyle.padRight(s.getMaxGroupSize() + "명", 14);
                System.out.println(row);
            }
            System.out.println("--------------------------------------------------------------------------");
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
            boolean backToStoreSelection = false;

            if (waitingService.hasActiveWaitingAtStore(customer.getCustomerId(), selectedStore.getStoreId())) {
                System.out.println(ConsoleStyle.warning(selectedStore.getStoreName() + "에는 이미 진행 중인 대기가 있습니다."));
                System.out.println(ConsoleStyle.info("현재 대기 현황에서 확인하거나 취소 후 다시 등록해주세요."));
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

                if (waitingService.exceedsMaxCapacity(selectedStore, peopleCount)) {
                    System.out.println(
                        ConsoleStyle.error(selectedStore.getStoreName() + "의 매장 좌석 수는 "
                            + selectedStore.getMaxCapacity() + "석입니다.")
                    );
                    continue;
                }

                if (waitingService.exceedsMaxGroupSize(selectedStore, peopleCount)) {
                    System.out.println(
                        ConsoleStyle.error("한 팀 최대 이용 가능 인원은 "
                            + selectedStore.getMaxGroupSize() + "명입니다. 대기 등록이 불가합니다.")
                    );
                    continue;
                }
                break;
            }

            if (backToStoreSelection) {
                continue;
            }

            List<Menu> menus = waitingService.getMenusByStoreId(selectedStore.getStoreId());
            Map<Integer, Integer> selectedMenus = new LinkedHashMap<>();
            boolean proceedToRegister = false;

            if (!menus.isEmpty()) {
                boolean editMode = false;
                String menuNotice = null;
                while (true) {
                    while (true) {
                        clearConsole();
                        renderMenuSelectionGuide(selectedStore, menus, peopleCount);
                        int remainingQuantity =
                            selectedStore.getMaxGroupSize() - waitingService.getTotalSelectedMenuQuantity(selectedMenus);
                        if (menuNotice != null) {
                            System.out.println(menuNotice);
                            System.out.println();
                            menuNotice = null;
                        }
                        renderCurrentOrderSummary(menus, selectedMenus, selectedStore.getMaxGroupSize(), "현재 주문내역");

                        if (!editMode && remainingQuantity == 0 && !selectedMenus.isEmpty()) {
                            System.out.println(ConsoleStyle.info("주문 가능 수량이 모두 채워져 주문내역 확인 단계로 이동합니다."));
                            break;
                        }

                        System.out.print("입력: ");
                        String line = scanner.nextLine().trim();

                        if ("B".equalsIgnoreCase(line)) {
                            backToStoreSelection = true;
                            break;
                        }

                        if (line.equals("0")) {
                            if (selectedMenus.isEmpty()) {
                                menuNotice = ConsoleStyle.warning("메뉴를 1개 이상 선택해야 합니다. 가게 선택으로 돌아가려면 B를 입력하세요.");
                                continue;
                            }
                            menuNotice = null;
                            break;
                        }

                        String[] parts = line.split("\\s+");
                        if (parts.length != 2) {
                            menuNotice = ConsoleStyle.error("형식이 올바르지 않습니다. 예) 1 2");
                            continue;
                        }

                        if (!ValidationUtil.isPositiveInteger(parts[0])) {
                            menuNotice = ConsoleStyle.error("올바른 번호를 입력하세요.");
                            continue;
                        }

                        int menuNumber = Integer.parseInt(parts[0]);
                        if (!ValidationUtil.isInteger(parts[1])) {
                            menuNotice = ConsoleStyle.error("올바른 수량을 입력하세요.");
                            continue;
                        }

                        int quantity = Integer.parseInt(parts[1]);

                        if (!ValidationUtil.isInRange(menuNumber, 1, menus.size())) {
                            menuNotice = ConsoleStyle.error("올바른 번호를 입력하세요.");
                            continue;
                        }

                        int menuIndex = menuNumber - 1;
                        Menu selected = menus.get(menuIndex);

                        if (!selected.isAvailable()) {
                            menuNotice = ConsoleStyle.error(selected.getMenuName() + "은(는) 현재 품절입니다.");
                            continue;
                        }

                        if (quantity < 0) {
                            menuNotice = ConsoleStyle.error("수량은 0 이상으로 입력해주세요.");
                            continue;
                        }

                        if (quantity == 0) {
                            if (selectedMenus.containsKey(selected.getMenuId())) {
                                selectedMenus.remove(selected.getMenuId());
                            } else {
                                menuNotice = ConsoleStyle.warning("해당 메뉴는 현재 주문내역에 없습니다.");
                            }
                            continue;
                        }

                        int currentSelectedQuantity = selectedMenus.getOrDefault(selected.getMenuId(), 0);
                        int nextTotalQuantity =
                            waitingService.getTotalSelectedMenuQuantity(selectedMenus) + quantity;
                        if (nextTotalQuantity > selectedStore.getMaxGroupSize()) {
                            menuNotice = ConsoleStyle.error(
                                "총 주문 수량은 한 팀 최대 이용 가능 인원인 " + selectedStore.getMaxGroupSize() + "개를 초과할 수 없습니다."
                            );
                            continue;
                        }

                        selectedMenus.put(selected.getMenuId(), currentSelectedQuantity + quantity);
                        if (nextTotalQuantity == selectedStore.getMaxGroupSize()) {
                            menuNotice = ConsoleStyle.info("주문 가능 수량이 모두 채워져 주문내역 확인 단계로 이동합니다.");
                            break;
                        }
                    }

                    if (backToStoreSelection) {
                        break;
                    }

                    while (true) {
                        renderOrderConfirmation(selectedStore, menus, selectedMenus, peopleCount);
                        System.out.println("1. 대기 등록 진행");
                        System.out.println("2. 주문 수정");
                        System.out.println("0. 가게 선택으로 돌아가기");
                        System.out.print("선택 >> ");

                        String confirmInput = scanner.nextLine().trim();

                        if ("1".equals(confirmInput)) {
                            proceedToRegister = true;
                            break;
                        }

                        if ("2".equals(confirmInput)) {
                            editMode = true;
                            break;
                        }

                        if ("0".equals(confirmInput)) {
                            backToStoreSelection = true;
                            break;
                        }

                        System.out.println(ConsoleStyle.error("올바른 메뉴 번호를 입력해주세요."));
                    }

                    if (proceedToRegister || backToStoreSelection) {
                        break;
                    }
                }
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

    private void renderMenuSelectionGuide(Store store, List<Menu> menus, int peopleCount) {
        System.out.println();
        System.out.println(ConsoleStyle.divider());
        System.out.println(ConsoleStyle.centeredTitle(store.getStoreName() + " 메뉴", 40));
        System.out.println(ConsoleStyle.divider());
        System.out.println(ConsoleStyle.info(
            "매장 좌석 수: " + store.getMaxCapacity() + "석 | 한 팀 최대 이용 가능 인원: "
                + store.getMaxGroupSize() + "명 | 현재 입력 인원: " + peopleCount + "명"
        ));
        String menuHeader = ConsoleStyle.padRight("번호", 6)
            + ConsoleStyle.padRight("메뉴명", 20)
            + ConsoleStyle.padRight("가격", 14)
            + ConsoleStyle.padRight("상태", 12);
        System.out.println(menuHeader);
        System.out.println("--------------------------------------------------------");
        for (int i = 0; i < menus.size(); i++) {
            Menu m = menus.get(i);
            String status = m.isAvailable() ? "주문 가능" : "품절";
            String row = ConsoleStyle.padRight(String.valueOf(i + 1), 6)
                + ConsoleStyle.padRight(m.getMenuName(), 20)
                + ConsoleStyle.padRight(m.getPrice() + "원", 14)
                + ConsoleStyle.padRight(status, 12);
            System.out.println(row);
        }
        System.out.println("--------------------------------------------------------");
        System.out.println();
        System.out.println(ConsoleStyle.title("주문 방법"));
        System.out.println("┌──────────────────────────────────────┐");
        System.out.println("│ 입력 형식: 메뉴번호 수량             │");
        System.out.println("│ 추가 예시: 1 2                       │");
        System.out.println("│ 삭제 예시: 1 0                       │");
        System.out.println("│ 주문 완료: 0                         │");
        System.out.println("│ 가게 목록으로 돌아가기: B            │");
        System.out.println(
            "│ " + ConsoleStyle.padRight("주문 가능 최대 수량: " + store.getMaxGroupSize() + "개", 37) + "│"
        );
        System.out.println("└──────────────────────────────────────┘");
    }

    private void renderCurrentOrderSummary(List<Menu> menus, Map<Integer, Integer> selectedMenus, int maxQuantity, String title) {
        System.out.println();
        System.out.println(ConsoleStyle.title("─────────────" + title + "─────────────"));

        if (selectedMenus.isEmpty()) {
            System.out.println("- 없음");
            System.out.println(ConsoleStyle.info("총 주문 수량: 0개 / 최대 " + maxQuantity + "개"));
            return;
        }

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
        System.out.println(
            ConsoleStyle.info(
                "총 주문 수량: " + waitingService.getTotalSelectedMenuQuantity(selectedMenus) + "개 / 최대 " + maxQuantity + "개"
            )
        );
    }

    private void renderOrderConfirmation(Store store, List<Menu> menus, Map<Integer, Integer> selectedMenus, int peopleCount) {
        clearConsole();
        System.out.println();
        System.out.println(ConsoleStyle.divider());
        System.out.println(ConsoleStyle.centeredTitle("주문내역 확인", 40));
        System.out.println(ConsoleStyle.divider());
        System.out.println("가게      : " + store.getStoreName());
        System.out.println("입력 인원 : " + peopleCount + "명");
        renderCurrentOrderSummary(menus, selectedMenus, store.getMaxGroupSize(), "주문내역");
        System.out.println(ConsoleStyle.divider());
    }

    private void clearConsole() {
        System.out.print("\033[2J\033[3J\033[H");
        System.out.flush();
    }
}
