package ui;

import common.PhoneNumberUtil;
import common.ValidationUtil;
import common.WaitingStatus;
import dto.Customer;
import dto.Store;
import service.CustomerService;
import dto.Waiting;
import service.WaitingService;
import java.io.IOException;
import java.util.List;


import java.util.Scanner;

public class CustomerUI {

    private final CustomerService customerService = new CustomerService();
    private final Scanner scanner;
    private final WaitingRegisterUI waitingRegisterUI;
    private final WaitingService waitingService = new WaitingService();


    public CustomerUI(Scanner scanner) {
        this.scanner = scanner;
        this.waitingRegisterUI = new WaitingRegisterUI(scanner);
    }

    public boolean start() {
        Customer customer = handleLoginOrRegister();
        if (customer == null) {
            System.out.println("비밀번호를 " + customerService.getMaxPasswordAttempts() + "회 이상 틀렸습니다. 프로그램을 종료합니다.");
            return false;
        }
        return showMainMenu(customer);
    }

    private Customer handleLoginOrRegister() {
        System.out.println("=== QueueX에 오신 것을 환영합니다 ===");
        System.out.print("전화번호를 입력하세요: ");
        String phone = scanner.nextLine().trim();

        if (!PhoneNumberUtil.isValid(phone)) {
            System.out.println("올바른 전화번호 형식이 아닙니다. (예: 01012345678)");
            return null;
        }
        phone = PhoneNumberUtil.normalize(phone);

        // 신규 고객 분기
        if (!customerService.isRegistered(phone)) {
            System.out.println("등록되지 않은 번호입니다. 신규 가입을 진행합니다.");
            System.out.print("사용할 비밀번호를 입력하세요: ");
            String password = scanner.nextLine().trim();
            Customer newCustomer = customerService.register(phone, password);

            if (newCustomer == null) {
                System.out.println("회원가입에 실패했습니다. 다시 시도해주세요.");
                return null;
            }

            System.out.println("가입이 완료되었습니다. 환영합니다!");
            return newCustomer;
        }

        // 기존 고객 로그인 (최대 3회 시도)
        int attempts = 0;
        while (attempts < customerService.getMaxPasswordAttempts()) {
            System.out.print("비밀번호를 입력하세요: ");
            String password = scanner.nextLine().trim();

            Customer customer = customerService.login(phone, password);
            if (customer != null) {
                System.out.println("\n고객 로그인에 성공했습니다.");
                return customer;
            }

            attempts++;
            int remaining = customerService.getMaxPasswordAttempts() - attempts;
            if (remaining > 0) {
                System.out.println("비밀번호가 일치하지 않습니다. 남은 시도 횟수: " + remaining + "회");
            }
        }
        return null;
    }


    private boolean showMyWaiting(Customer customer) {
        while (true) {
            List<Waiting> waitingList = waitingService.getWaitingByCustomerId(customer.getCustomerId());
            renderWaitingDashboard(waitingList);

            String input = waitForDashboardInput();

            if (input.isEmpty()) {
                continue;
            }

            switch (input) {
                case "1":
                    cancelMyWaiting(customer);
                    break;
                case "2":
                    return true;
                case "3":
                    System.out.println("프로그램을 종료합니다.");
                    return false;
                default:
                    System.out.println("올바른 메뉴 번호를 입력해주세요.");
                    sleepSilently(1200);
            }
        }
    }

private String buildWaitingGuideMessage(String status, int currentPosition) {
    if (WaitingStatus.CALLED.equals(status)) {
        return "지금 입장해주세요!";
    }

    if (WaitingStatus.WAITING.equals(status) && currentPosition <= 3) {
        return "곧 입장 순서입니다. 가게 앞에서 대기해주세요!";
    }

    if (WaitingStatus.WAITING.equals(status)) {
        return "현재 대기 중입니다.";
    }

    return "현재 상태를 확인해주세요.";
}

private void renderWaitingDashboard(List<Waiting> waitingList) {
    clearConsole();
    System.out.println("=== 내 대기 현황 ===");

    if (waitingList.isEmpty()) {
        System.out.println("현재 등록된 대기가 없습니다.");
    } else {
        for (Waiting w : waitingList) {
            Store store = waitingService.getStoreById(w.getStoreId());
            String storeName = store != null ? store.getStoreName() : "알 수 없음";
            List<String> orderSummaries = waitingService.getOrderSummariesByWaitingId(w.getWaitingId());
            int currentPosition = waitingService.getCurrentPosition(w.getStoreId(), w.getWaitingNumber());

            System.out.println("가게명   : " + storeName);
            System.out.println("대기 번호 : " + w.getWaitingNumber());
            System.out.println("내 순서  : " + currentPosition + "번째");
            System.out.println("인원수   : " + w.getPeopleCount() + "명");
            System.out.println("안내     : " + buildWaitingGuideMessage(w.getStatus(), currentPosition));
            System.out.println("등록 시각 : " + w.getCreatedAt());
            System.out.println("주문내역 : " + (orderSummaries.isEmpty() ? "없음" : String.join(", ", orderSummaries)));
            System.out.println("--------------------");
        }
    }

    System.out.println("1. 대기 취소");
    System.out.println("2. 메뉴로 이동");
    System.out.println("3. 종료");
    System.out.print("선택 >> ");
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

private void cancelMyWaiting(Customer customer) {
    List<Waiting> waitingList = waitingService.getWaitingByCustomerId(customer.getCustomerId());

    if (waitingList.isEmpty()) {
        System.out.println("취소할 대기가 없습니다.");
        return;
    }

    System.out.println("\n=== 취소할 대기 선택 ===");
    for (int i = 0; i < waitingList.size(); i++) {
        Waiting w = waitingList.get(i);
        System.out.println((i + 1) + ". 대기 번호: " + w.getWaitingNumber()
            + " | 인원수: " + w.getPeopleCount() + "명"
            + " | 등록 시각: " + w.getCreatedAt());
    }
    System.out.println("0. 취소 없이 돌아가기");
    System.out.print("선택: ");

    String input = scanner.nextLine().trim();
    if ("0".equals(input)) {
        return;
    }

    if (!ValidationUtil.isPositiveInteger(input)) {
        System.out.println("올바른 번호를 입력하세요.");
        return;
    }

    int selectedNumber = Integer.parseInt(input);
    if (!ValidationUtil.isInRange(selectedNumber, 1, waitingList.size())) {
        System.out.println("올바른 번호를 입력하세요.");
        return;
    }

    int index = selectedNumber - 1;
    Waiting selected = waitingList.get(index);

    System.out.print("대기 번호 " + selected.getWaitingNumber() + "번을 정말 취소하시겠습니까? (Y/N) >> ");
    String confirm = scanner.nextLine().trim().toUpperCase();

    if ("N".equals(confirm)) {
        System.out.println("대기 취소를 취소했습니다.");
        return;
    }

    if (!"Y".equals(confirm)) {
        System.out.println("올바른 입력이 아닙니다. 취소를 진행하지 않습니다.");
        return;
    }

    boolean result = waitingService.cancelWaiting(selected.getWaitingId(), customer.getCustomerId());

    if (result) {
        System.out.println("대기 번호 " + selected.getWaitingNumber() + "번이 취소되었습니다.");
    } else {
        System.out.println("대기 취소에 실패했습니다. 다시 시도해주세요.");
    }
}


    private boolean showMainMenu(Customer customer) {
        while (true) {
            System.out.println("""
                
                [고객 메뉴]
                1. 가게 선택
                2. 내 대기 조회
                3. 종료
                선택 >>
                """);

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
                case "3":
                    System.out.println("프로그램을 종료합니다.");
                    return false;
                default:
                    System.out.println("올바른 메뉴 번호를 입력해주세요.");
            }
        }
    }
}
