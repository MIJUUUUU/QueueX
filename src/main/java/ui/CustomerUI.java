package ui;

import dto.Customer;
import service.CustomerService;

import java.util.Scanner;

public class CustomerUI {

    private final Scanner scanner = new Scanner(System.in);
    private final CustomerService customerService = new CustomerService();
    private final LoginUI loginUI = new LoginUI(scanner, customerService);
    private final WaitingRegisterUI waitingRegisterUI = new WaitingRegisterUI(scanner);

    public void start() {
        Customer customer = loginUI.handle();
        if (customer == null) {
            System.out.println("비밀번호를 " + customerService.getMaxPasswordAttempts() + "회 이상 틀렸습니다. 프로그램을 종료합니다.");
            return;
        }
        showMainMenu(customer);
    }

    private void showMainMenu(Customer customer) {
        while (true) {
            System.out.println("\n=== 고객 메뉴 ===");
            System.out.println("1. 대기 등록");
            System.out.println("0. 로그아웃");
            System.out.print("선택: ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> waitingRegisterUI.handle(customer);
                case "0" -> {
                    System.out.println("로그아웃합니다.");
                    return;
                }
                default -> System.out.println("올바른 번호를 입력하세요.");
            }
        }
    }
}