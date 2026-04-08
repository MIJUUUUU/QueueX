package ui;

import dto.Customer;
import service.CustomerService;

import java.util.Scanner;

import common.PhoneNumberUtil;

public class LoginUI {

    private final Scanner scanner;
    private final CustomerService customerService;

    public LoginUI(Scanner scanner, CustomerService customerService) {
        this.scanner = scanner;
        this.customerService = customerService;
    }

    public Customer handle() {
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
                System.out.println("로그인 성공! 어서오세요.");
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
}
