package ui;

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

  // 좌석 운영 시스템 시작
  public void startSeatFlow(Store store) {
    while (true) {
      System.out.print("수용 인원 입력 >> ");
      String input = s.nextLine().trim();

      if (!ValidationUtil.isPositiveInteger(input)) {
        System.out.println("잘못된 입력입니다. 숫자를 입력해주세요.");
        continue;
      }

      int seatCount = Integer.parseInt(input);
      runSeatCycle(store, seatCount);
      return;
    }
  }

  // 추천 -> 호출 -> 입장/노쇼 처리를 반복
  private void runSeatCycle(Store store, int seatCount) {
    while (true) {
      List<Waiting> waitingList = waitingService.getWaitingByStoreId(store.getStoreId());
      List<Waiting> recommendedList = recommendationService.recommend(waitingList, seatCount);

      if (recommendedList.isEmpty()) {
        if (!handleEmptyWaiting()) {
          return;
        }
        return;
      }

      System.out.println("===== 추천 순위 =====");
      for (Waiting waiting : recommendedList) {
        System.out.println("대기 " + waiting.getWaitingNumber() + "번 / " + waiting.getPeopleCount() + "명");
      }

      Waiting target = recommendedList.get(0);

      System.out.println();
      System.out.println("다음 손님을 호출할까요? [대기 " + target.getWaitingNumber() + "번 / " + target.getPeopleCount() + "명]");
      System.out.println("1. 호출");
      System.out.println("0. 취소");
      System.out.print("선택 >> ");

      String select = s.nextLine().trim();

      if ("0".equals(select)) {
        return;
      }

      if (!"1".equals(select)) {
        System.out.println("잘못된 입력입니다. 다시 시도해주세요.");
        continue;
      }

      boolean called = waitingService.callWaiting(target.getWaitingId());

      if (!called) {
        System.out.println("호출 처리에 실패했습니다.");
        return;
      }

      System.out.println("호출 완료 !!");

      if (!handleCalledWaiting(target)) {
        return;
      }
    }
  }

  // 현재 호출된 손님에 대해 입장/노쇼/뒤로가기 선택 처리
  private boolean handleCalledWaiting(Waiting waiting) {
    while (true) {
      System.out.println("===== 현재 호출 =====");
      System.out.println("대기 " + waiting.getWaitingNumber() + "번 / " + waiting.getPeopleCount() + "명");
      System.out.println("1. 입장 처리");
      System.out.println("2. 노쇼 처리");
      System.out.println("0. 관리자 메뉴");
      System.out.print("선택 >> ");

      String input = s.nextLine().trim();

      switch (input) {
        case "1":
          if (waitingService.enterWaiting(waiting.getWaitingId())) {
            System.out.println("입장 처리 완료 !!");
          } else {
            System.out.println("입장 처리에 실패했습니다.");
          }
          return true;
        case "2":
          if (waitingService.noshowWaiting(waiting.getWaitingId())) {
            System.out.println("노쇼 처리 완료 !!");
          } else {
            System.out.println("노쇼 처리에 실패했습니다.");
          }
          return true;
        case "0":
          return false;
        default:
          System.out.println("잘못된 입력입니다. 다시 시도해주세요.");
      }
    }
  }

  // 추천 가능한 대기 손님이 없을 때 처리
  private boolean handleEmptyWaiting() {
    while (true) {
      System.out.println("대기 손님이 없습니다.");
      System.out.print("관리자 메뉴로 돌아가시겠습니까? (Y : 돌아가기 / N : 좌석 운영 종료) >> ");
      String input = s.nextLine().trim().toUpperCase();

      if ("Y".equals(input)) {
        System.out.println("관리자 메뉴로 돌아갑니다.");
        return true;
      }

      if ("N".equals(input)) {
        System.out.println("좌석 운영을 종료합니다.");
        return false;
      }

      System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
    }
  }
}
