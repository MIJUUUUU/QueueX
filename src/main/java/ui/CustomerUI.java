package ui;

import common.ConsoleStyle;
import common.PhoneNumberUtil;
import common.ValidationUtil;
import common.WaitingStatus;
import dto.Customer;
import dto.Store;
import dto.Waiting;
import service.CustomerService;
import service.WaitingService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class CustomerUI {

    private final CustomerService customerService = new CustomerService();
    private final Scanner scanner;
    private final WaitingRegisterUI waitingRegisterUI;
    private final WaitingService waitingService = new WaitingService();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public CustomerUI(Scanner scanner) {
        this.scanner = scanner;
        this.waitingRegisterUI = new WaitingRegisterUI(scanner);
    }

    public boolean start() {
        Customer customer = handleLoginOrRegister();
        if (customer == null) {
            return false;
        }
        return showMainMenu(customer);
    }

    private Customer handleLoginOrRegister() {
        int phoneAttempts = 0;

        while (true) {
            System.out.println(ConsoleStyle.divider());
            System.out.println(ConsoleStyle.title("QueueX에 오신 것을 환영합니다"));
            System.out.println(ConsoleStyle.divider());
            System.out.print("전화번호를 입력하세요: ");
            String phone = scanner.nextLine().trim();

            if (!PhoneNumberUtil.isValid(phone)) {
                phoneAttempts++;
                System.out.println(ConsoleStyle.error("올바른 전화번호 형식이 아닙니다. (예: 01012345678)"));
                if (phoneAttempts >= 3) {
                    System.out.println(ConsoleStyle.error("전화번호 형식을 3회 이상 틀렸습니다. 프로그램을 종료합니다."));
                    return null;
                }

                System.out.println(ConsoleStyle.warning("남은 시도 횟수: " + (3 - phoneAttempts) + "회"));
                System.out.println();
                continue;
            }
            phone = PhoneNumberUtil.normalize(phone);
            phoneAttempts = 0;

            if (!customerService.isRegistered(phone)) {
                System.out.println(ConsoleStyle.info("등록되지 않은 번호입니다. 신규 가입을 진행합니다."));
                System.out.print("사용할 비밀번호를 입력하세요: ");
                String password = scanner.nextLine().trim();
                Customer newCustomer = customerService.register(phone, password);

                if (newCustomer == null) {
                    System.out.println(ConsoleStyle.error("회원가입에 실패했습니다. 다시 시도해주세요."));
                    return null;
                }

                System.out.println(ConsoleStyle.success("가입이 완료되었습니다. 환영합니다!"));
                return newCustomer;
            }

            int attempts = 0;
            while (attempts < customerService.getMaxPasswordAttempts()) {
                System.out.print("비밀번호를 입력하세요: ");
                String password = scanner.nextLine().trim();

                Customer customer = customerService.login(phone, password);
                if (customer != null) {
                    System.out.println();
                    System.out.println(ConsoleStyle.success("고객 로그인에 성공했습니다."));
                    return customer;
                }

                attempts++;
                int remaining = customerService.getMaxPasswordAttempts() - attempts;
                if (remaining > 0) {
                    System.out.println(ConsoleStyle.error("비밀번호가 일치하지 않습니다. 남은 시도 횟수: " + remaining + "회"));
                }
            }
            System.out.println(ConsoleStyle.error(
                "비밀번호를 " + customerService.getMaxPasswordAttempts() + "회 이상 틀렸습니다. 프로그램을 종료합니다."
            ));
            return null;
        }
    }

    private boolean showMainMenu(Customer customer) {
        while (true) {
            System.out.println();
            System.out.println(ConsoleStyle.divider());
            System.out.println(ConsoleStyle.title("고객 메뉴"));
            System.out.println(ConsoleStyle.divider());
            System.out.println("1. 가게 선택");
            System.out.println("2. 내 대기 조회");
            System.out.println("0. 종료");
            System.out.println(ConsoleStyle.divider());
            System.out.print("선택 >> ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    waitingRegisterUI.handle(customer);
                    break;
                case "2":
                    if (!showMyWaiting(customer)) {
                        return false;
                    }
                    break;
                case "0":
                    System.out.println(ConsoleStyle.info("프로그램을 종료합니다."));
                    return false;
                default:
                    System.out.println(ConsoleStyle.error("올바른 메뉴 번호를 입력해주세요."));
            }
        }
    }

    private boolean showMyWaiting(Customer customer) {
        String lastSnapshot = null;

        while (true) {
            List<Waiting> waitingList = waitingService.getWaitingByCustomerId(customer.getCustomerId());
            String currentSnapshot = buildWaitingSnapshot(waitingList);

            if (!currentSnapshot.equals(lastSnapshot)) {
                renderWaitingDashboard(waitingList);
                lastSnapshot = currentSnapshot;
            }

            String input = waitForDashboardInput();
            if (input.isEmpty()) {
                continue;
            }

            switch (input) {
                case "1":
                    cancelMyWaiting(customer);
                    lastSnapshot = null;
                    break;
                case "2":
                    return true;
                case "0":
                    System.out.println(ConsoleStyle.info("프로그램을 종료합니다."));
                    return false;
                default:
                    System.out.println(ConsoleStyle.error("올바른 메뉴 번호를 입력해주세요."));
                    sleepSilently(1200);
            }
        }
    }

    private void renderWaitingDashboard(List<Waiting> waitingList) {
        clearConsole();
        System.out.println(ConsoleStyle.divider());
        System.out.println(ConsoleStyle.title("내 대기 현황"));
        System.out.println(ConsoleStyle.divider());

        if (waitingList.isEmpty()) {
            System.out.println();
            System.out.println(ConsoleStyle.warning("현재 등록된 대기가 없습니다."));
            System.out.println(ConsoleStyle.info("가게를 선택해 새 대기를 등록할 수 있습니다."));
            System.out.println();
            System.out.println("┌──────────────────────────────────────┐");
            System.out.println("│  아직 진행 중인 대기가 없습니다.     │");
            System.out.println("│  1번 메뉴에서 대기를 등록해보세요.   │");
            System.out.println("└──────────────────────────────────────┘");
            System.out.println(ConsoleStyle.divider());
        } else {
            for (Waiting w : waitingList) {
                Store store = waitingService.getStoreById(w.getStoreId());
                String storeName = store != null ? store.getStoreName() : "알 수 없음";
                List<String> orderSummaries = waitingService.getOrderSummariesByWaitingId(w.getWaitingId());
                int currentPosition = waitingService.getCurrentPosition(w.getStoreId(), w.getWaitingNumber());

                System.out.println();
                System.out.println(ConsoleStyle.highlight("[" + storeName + "]"));
                System.out.println(
                    w.getWaitingNumber() + "번"
                        + " | " + currentPosition + "번째"
                        + " | " + w.getPeopleCount() + "명"
                        + " | " + formatDateTime(w.getCreatedAt())
                );
                System.out.println("안내: " + buildWaitingGuideMessage(w.getStatus(), currentPosition));
                System.out.println("주문: " + (orderSummaries.isEmpty() ? "없음" : String.join(", ", orderSummaries)));
                System.out.println("----------------------------------------");
            }
            System.out.println();
            System.out.println(ConsoleStyle.divider());
        }

        System.out.println("1. 대기 취소");
        System.out.println("2. 메뉴로 이동");
        System.out.println("0. 종료");
        System.out.print("선택 >> ");
    }

    private String buildWaitingGuideMessage(String status, int currentPosition) {
        if (WaitingStatus.CALLED.equals(status)) {
            return ConsoleStyle.success("지금 입장해주세요!");
        }

        if (WaitingStatus.WAITING.equals(status) && currentPosition <= 3) {
            return ConsoleStyle.warning("곧 입장 순서입니다. 가게 앞에서 대기해주세요!");
        }

        if (WaitingStatus.WAITING.equals(status)) {
            return ConsoleStyle.info("현재 대기 중입니다.");
        }

        return ConsoleStyle.warning("현재 상태를 확인해주세요.");
    }

    private String buildWaitingSnapshot(List<Waiting> waitingList) {
        if (waitingList.isEmpty()) {
            return "EMPTY";
        }

        StringBuilder snapshot = new StringBuilder();
        for (Waiting w : waitingList) {
            int currentPosition = waitingService.getCurrentPosition(w.getStoreId(), w.getWaitingNumber());
            snapshot.append(w.getWaitingId()).append("|")
                .append(w.getStatus()).append("|")
                .append(currentPosition).append(";");
        }
        return snapshot.toString();
    }

    private String waitForDashboardInput() {
        long deadline = System.currentTimeMillis() + 3000;

        while (System.currentTimeMillis() < deadline) {
            try {
                if (System.in.available() > 0) {
                    return scanner.nextLine().trim();
                }
                Thread.sleep(200);
            } catch (IOException e) {
                return "";
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "3";
            }
        }

        return "";
    }

    private void cancelMyWaiting(Customer customer) {
        List<Waiting> waitingList = waitingService.getWaitingByCustomerId(customer.getCustomerId());

        if (waitingList.isEmpty()) {
            System.out.println(ConsoleStyle.warning("취소할 대기가 없습니다."));
            return;
        }

        System.out.println();
        System.out.println(ConsoleStyle.divider());
        System.out.println(ConsoleStyle.title("취소할 대기 선택"));
        System.out.println(ConsoleStyle.divider());
        System.out.printf("%-4s %-8s %-8s %-18s%n", "번호", "대기번호", "인원수", "등록 시각");
        System.out.println("----------------------------------------");
        for (int i = 0; i < waitingList.size(); i++) {
            Waiting w = waitingList.get(i);
            System.out.printf(
                "%-4d %-8d %-8s %-18s%n",
                i + 1,
                w.getWaitingNumber(),
                w.getPeopleCount() + "명",
                formatDateTime(w.getCreatedAt())
            );
        }
        System.out.println("----------------------------------------");
        System.out.println("0. 취소 없이 돌아가기");
        System.out.print("선택 >> ");

        String input = scanner.nextLine().trim();
        if ("0".equals(input)) {
            return;
        }

        if (!ValidationUtil.isPositiveInteger(input)) {
            System.out.println(ConsoleStyle.error("올바른 번호를 입력하세요."));
            return;
        }

        int selectedNumber = Integer.parseInt(input);
        if (!ValidationUtil.isInRange(selectedNumber, 1, waitingList.size())) {
            System.out.println(ConsoleStyle.error("올바른 번호를 입력하세요."));
            return;
        }

        Waiting selected = waitingList.get(selectedNumber - 1);

        while (true) {
            System.out.print("대기 번호 " + selected.getWaitingNumber() + "번을 정말 취소하시겠습니까? (Y/N) >> ");
            String confirm = scanner.nextLine().trim().toUpperCase();

            if ("N".equals(confirm)) {
                System.out.println(ConsoleStyle.info("대기 취소를 취소했습니다."));
                return;
            }

            if ("Y".equals(confirm)) {
                break;
            }

            System.out.println(ConsoleStyle.error("Y 또는 N을 입력해주세요."));
        }

        boolean result = waitingService.cancelWaiting(selected.getWaitingId(), customer.getCustomerId());
        if (result) {
            System.out.println(ConsoleStyle.success("대기 번호 " + selected.getWaitingNumber() + "번이 취소되었습니다."));
        } else {
            System.out.println(ConsoleStyle.error("대기 취소에 실패했습니다. 다시 시도해주세요."));
        }
    }

    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void sleepSilently(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "-";
        }
        return dateTime.format(DATE_TIME_FORMATTER);
    }
}
