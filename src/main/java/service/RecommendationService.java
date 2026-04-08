package service;

import dto.Waiting;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RecommendationService {
  // 수용 인원에 맞춰 추천 순위 계산
  public List<Waiting> recommend(List<Waiting> waitingList, int seatCount) {
    List<Waiting> recommended = new ArrayList<>(waitingList);

    // 6인 입력되면 6명 손님 우선 추천
    if (seatCount == 6) {
      recommended.sort(
          Comparator.comparing((Waiting w) -> w.getPeopleCount() == 6 ? 0 : 1)
              .thenComparing(Waiting::getWaitingNumber)
      );
      return recommended;
    }

    // 6인 아닐 경우 FIFO 기준으로 추천
    recommended.sort(Comparator.comparing(Waiting::getWaitingNumber));
    return recommended;
  }
}