package service;

import dto.Waiting;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RecommendationService {
  // 수용 인원에 맞춰 추천 순위 계산
  public List<Waiting> recommend(List<Waiting> waitingList, int seatCount) {
    List<Waiting> recommended = new ArrayList<>();

    for (Waiting waiting : waitingList) {
      if (waiting.getPeopleCount() <= seatCount) {
        recommended.add(waiting);
      }
    }

    recommended.sort(Comparator.comparing(Waiting::getWaitingNumber));
    return recommended;
  }
}
