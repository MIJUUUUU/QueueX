package ui;

import dto.Customer;
import service.CustomerService;

import java.util.Scanner;

public class CustomerUI {

    private final CustomerService customerService = new CustomerService();
    private final Scanner scanner;
    private final WaitingRegisterUI waitingRegisterUI;

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
        showMainMenu(customer);
        return true;
    }

    private Customer handleLoginOrRegister() {
        System.out.println("=== QueueX에 오신 것을 환영합니다 ===");
        System.out.print("전화번호를 입력하세요: ");
        String phone = scanner.nextLine().trim();
        

        // 신규 고객 분기
        if (!customerService.isRegistered(phone)) {
            System.out.println("등록되지 않은 번호입니다. 신규 가입을 진행합니다.");
            System.out.print("사용할 비밀번호를 입력하세요: ");
            String password = scanner.nextLine().trim();

            Customer newCustomer = customerService.login(phone, password);
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
                System.out.println("\n로그인 성공! 어서오세요.");
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

    private void showMainMenu(Customer customer) {
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
                    System.out.println("내 대기 조회 기능은 추후 구현 예정입니다.");
                    break;
                case "3":
                    System.out.println("고객 메뉴를 종료합니다.");
                    return;
                default:
                    System.out.println("올바른 메뉴 번호를 입력해주세요.");
            }
        }
    }
}
