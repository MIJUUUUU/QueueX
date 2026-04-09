package service;

import dto.Waiting;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RecommendationService {
  // 수용 인원에 맞춰 추천 순위 계산
  public List<Waiting> recommend(List<Waiting> waitingList, int seatCount) {
    List<Waiting> regularRecommended = new ArrayList<>();

    for (Waiting waiting : waitingList) {
      if (waiting.getPeopleCount() <= seatCount) {
        regularRecommended.add(waiting);
      }
    }

    regularRecommended.sort(Comparator.comparing(Waiting::getWaitingNumber));

    if (seatCount < 6) {
      return regularRecommended;
    }

    List<Waiting> groupPriority = new ArrayList<>();
    List<Waiting> regularPriority = new ArrayList<>();

    for (Waiting waiting : regularRecommended) {
      if (waiting.getPeopleCount() >= 6) {
        groupPriority.add(waiting);
      } else {
        regularPriority.add(waiting);
      }
    }

    List<Waiting> recommended = new ArrayList<>();
    recommended.addAll(groupPriority);
    recommended.addAll(regularPriority);
    return recommended;
  }
}
