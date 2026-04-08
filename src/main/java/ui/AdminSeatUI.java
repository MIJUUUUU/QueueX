package ui;

import common.ValidationUtil;
import dto.Store;
import dto.Waiting;
import service.RecommendationService;
import service.WaitingService;

import java.util.List;
import java.util.Scanner;

public class AdminSeatUI {
  private final Scanner s = new Scanner(System.in);
  private final WaitingService waitingService = new WaitingService();
  private final RecommendationService recommendationService = new RecommendationService();

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

      // 현재 대기 목록 조회
      List<Waiting> waitingList = waitingService.getWaitingByStoreId(store.getStoreId());
      // 추천 순위 계산
      List<Waiting> recommendedList = recommendationService.recommend(waitingList, seatCount);

      // 대기 손님 없을시 메시지 및 종료
      if (recommendedList.isEmpty()) {
        System.out.println("추천 가능한 대기 손님이 없습니다.");
        return;
      }

      // 추천 순위 출력
      System.out.println("===== 추천 순위 =====");
      for (Waiting waiting : recommendedList) {
        System.out.println("대기 " + waiting.getWaitingNumber() + "번 / " + waiting.getPeopleCount() + "명");
      }


      // 가장 우선순위가 높은 대기 손님을 호출 대상으로 선택
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
      showCalledWaiting(target);
      return;
    }
  }

  // 현재 호출된 손님 정보 출력
  private void showCalledWaiting(Waiting waiting) {
    System.out.println("===== 현재 호출 =====");
    System.out.println("대기 " + waiting.getWaitingNumber() + "번 / " + waiting.getPeopleCount() + "명");
    System.out.println("1. 입장 처리");
    System.out.println("2. 노쇼 처리");
  }
}