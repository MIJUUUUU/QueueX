package ui;

import common.ConsoleStyle;
import common.ValidationUtil;
import dto.Store;
import dto.Waiting;
import service.RecommendationService;
import service.WaitingService;

import java.util.List;
import java.util.Scanner;

public class AdminSeatUI {
  private final Scanner s;
  private final WaitingService waitingService = new WaitingService();
  private final RecommendationService recommendationService = new RecommendationService();

  public AdminSeatUI(Scanner scanner) {
    this.s = scanner;
  }

  // 수용 인원 기준 운영 시스템 시작
  public void startSeatFlow(Store store) {
    while (true) {
      clearConsole();
      System.out.println(ConsoleStyle.divider());
      System.out.println(ConsoleStyle.title(store.getStoreName() + " 운영"));
      System.out.println(ConsoleStyle.divider());
      System.out.print("수용 인원 입력 >> ");
      String input = s.nextLine().trim();

      if (!ValidationUtil.isPositiveInteger(input)) {
        System.out.println(ConsoleStyle.error("잘못된 입력입니다. 숫자를 입력해주세요."));
        continue;
      }

      int seatCount = Integer.parseInt(input);

      if (seatCount > store.getMaxCapacity()) {
        System.out.println(
            ConsoleStyle.error(store.getStoreName() + "의 매장 좌석 수는 " + store.getMaxCapacity() + "석입니다.")
        );
        continue;
      }

      if (runSeatCycle(store, seatCount)) {
        return;
      }
    }
  }

  // 추천 -> 호출 -> 입장/노쇼 처리를 반복
  private boolean runSeatCycle(Store store, int seatCount) {
    while (true) {
      List<Waiting> waitingList = waitingService.getWaitingByStoreId(store.getStoreId());
      List<Waiting> recommendedList = recommendationService.recommend(waitingList, seatCount);

      if (recommendedList.isEmpty()) {
        if (waitingList.isEmpty()) {
          return handleEmptyWaiting();
        }

        return handleNoRecommendation(waitingList, seatCount);
      }

      System.out.println();
      System.out.println(ConsoleStyle.divider());
      System.out.println(ConsoleStyle.title("추천 순위"));
      System.out.println(ConsoleStyle.divider());
      System.out.printf("%-6s %-8s%n", "대기번호", "인원수");
      System.out.println("------------------");
      for (Waiting waiting : recommendedList) {
        System.out.printf("%-6s %-8s%n", waiting.getWaitingNumber() + "번", waiting.getPeopleCount() + "명");
      }
      System.out.println("------------------");

      Waiting target = recommendedList.get(0);

      System.out.println();
      System.out.println(ConsoleStyle.divider());
      System.out.println(ConsoleStyle.info("우선 호출 대상"));
      System.out.println(ConsoleStyle.divider());
      System.out.println("다음 손님을 호출할까요? [대기 " + target.getWaitingNumber() + "번 / " + target.getPeopleCount() + "명]");
      System.out.println("1. 호출");
      System.out.println("0. 취소");
      System.out.print("선택 >> ");

      String select = s.nextLine().trim();

      if ("0".equals(select)) {
        return true;
      }

      if (!"1".equals(select)) {
        System.out.println(ConsoleStyle.error("잘못된 입력입니다. 다시 시도해주세요."));
        continue;
      }

      boolean called = waitingService.callWaiting(target.getWaitingId());

      if (!called) {
        System.out.println(ConsoleStyle.error("호출 처리에 실패했습니다."));
        return true;
      }

      System.out.println();
      System.out.println(ConsoleStyle.success("호출이 완료되었습니다."));

      if (!handleCalledWaiting(target)) {
        return true;
      }

      return false;
    }
  }

  // 현재 호출된 손님에 대해 입장/노쇼 처리
  private boolean handleCalledWaiting(Waiting waiting) {
    while (true) {
      System.out.println();
      System.out.println(ConsoleStyle.divider());
      System.out.println(ConsoleStyle.title("현재 호출"));
      System.out.println(ConsoleStyle.divider());
      System.out.println("대기 " + waiting.getWaitingNumber() + "번 / " + waiting.getPeopleCount() + "명");
      System.out.println("1. 입장 처리");
      System.out.println("2. 노쇼 처리");
      System.out.print("선택 >> ");

      String input = s.nextLine().trim();

      switch (input) {
        case "1":
          if (waitingService.enterWaiting(waiting.getWaitingId())) {
            System.out.println();
            System.out.println(ConsoleStyle.success("입장 처리가 완료되었습니다."));
            System.out.println(ConsoleStyle.info("현재 좌석 상황에 맞게 수용 인원을 다시 입력해주세요."));
          } else {
            System.out.println(ConsoleStyle.error("입장 처리에 실패했습니다."));
          }
          return true;
        case "2":
          if (waitingService.noshowWaiting(waiting.getWaitingId())) {
            System.out.println();
            System.out.println(ConsoleStyle.success("노쇼 처리가 완료되었습니다."));
            System.out.println(ConsoleStyle.info("현재 좌석 상황에 맞게 수용 인원을 다시 입력해주세요."));
          } else {
            System.out.println(ConsoleStyle.error("노쇼 처리에 실패했습니다."));
          }
          return true;
        default:
          System.out.println(ConsoleStyle.error("입장 처리 또는 노쇼 처리만 선택할 수 있습니다."));
      }
    }
  }

  // 추천 가능한 대기 손님이 없을 때 처리
  private boolean handleEmptyWaiting() {
    while (true) {
      System.out.println();
      System.out.println(ConsoleStyle.warning("대기 손님이 없습니다."));
      System.out.print("관리자 메뉴로 돌아가시겠습니까? (Y : 돌아가기 / N : 운영 종료) >> ");
      String input = s.nextLine().trim().toUpperCase();

      if ("Y".equals(input)) {
        System.out.println("관리자 메뉴로 돌아갑니다.");
        return true;
      }

      if ("N".equals(input)) {
        System.out.println("운영을 종료합니다.");
        return true;
      }

      System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
    }
  }

  private boolean handleNoRecommendation(List<Waiting> waitingList, int seatCount) {
    while (true) {
      System.out.println();
      System.out.println(ConsoleStyle.warning("수용 인원 " + seatCount + "명 이하의 추천 가능한 대기 손님이 없습니다."));
      System.out.println();
      System.out.println(ConsoleStyle.title("현재 전체 대기 FIFO"));
      System.out.printf("%-6s %-8s%n", "대기번호", "인원수");
      System.out.println("------------------");
      for (Waiting waiting : waitingList) {
        System.out.printf("%-6s %-8s%n", waiting.getWaitingNumber() + "번", waiting.getPeopleCount() + "명");
      }
      System.out.println("------------------");
      System.out.println();
      System.out.println("1. 수용 인원 다시 입력");
      System.out.println("0. 관리자 메뉴");
      System.out.print("선택 >> ");

      String input = s.nextLine().trim();

      if ("1".equals(input)) {
        return false;
      }

      if ("0".equals(input)) {
        System.out.println("관리자 메뉴로 돌아갑니다.");
        return true;
      }

      System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
    }
  }

  private void clearConsole() {
    System.out.print("\033[2J\033[3J\033[H");
    System.out.flush();
  }
}
