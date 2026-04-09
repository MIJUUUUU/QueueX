package app;

import common.ConsoleStyle;
import common.DBUtil;
import ui.AdminUI;
import ui.CustomerUI;

import java.sql.Connection;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("QueueX application started.");

        try (Connection connection = DBUtil.getConnection()) {
            System.out.println("DB 연결 성공!");
        } catch (Exception e) {
            System.out.println("DB 연결 실패");
            e.printStackTrace();
            return;
        }

        Scanner scanner = new Scanner(System.in);
        CustomerUI customerUI = new CustomerUI(scanner);
        AdminUI adminUI = new AdminUI(scanner);

        while (true) {
            System.out.println();
            System.out.println(ConsoleStyle.divider());
            System.out.println(ConsoleStyle.title("사용자 유형 선택"));
            System.out.println(ConsoleStyle.divider());
            System.out.println("1. 고객");
            System.out.println("2. 관리자");
            System.out.println("3. 종료");
            System.out.println(ConsoleStyle.divider());
            System.out.print("선택 >> ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    if (!customerUI.start()) {
                        return;
                    }
                    break;
                case "2":
                    adminUI.adminStart();
                    break;
                case "3":
                    System.out.println("프로그램을 종료합니다.");
                    return;
                default:
                    System.out.println("올바른 메뉴 번호를 입력해주세요.");
            }
        }
    }
}
