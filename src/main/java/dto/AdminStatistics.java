
package dto;

import java.util.List;

public class AdminStatistics {
  private int totalVisitedCustomers;
  private int totalWaitingCount;
  private int noShowCount;
  private List<MenuStat> menuStats;

  public AdminStatistics(int totalVisitedCustomers, int totalWaitingCount, int noShowCount, List<MenuStat> menuStats) {
    this.totalVisitedCustomers = totalVisitedCustomers;
    this.totalWaitingCount = totalWaitingCount;
    this.noShowCount = noShowCount;
    this.menuStats = menuStats;
  }

  public int getTotalVisitedCustomers() {
    return totalVisitedCustomers;
  }

  public void setTotalVisitedCustomers(int totalVisitedCustomers) {
    this.totalVisitedCustomers = totalVisitedCustomers;
  }

  public int getTotalWaitingCount() {
    return totalWaitingCount;
  }

  public void setTotalWaitingCount(int totalWaitingCount) {
    this.totalWaitingCount = totalWaitingCount;
  }

  public int getNoShowCount() {
    return noShowCount;
  }

  public void setNoShowCount(int noShowCount) {
    this.noShowCount = noShowCount;
  }

  public List<MenuStat> getMenuStats() {
    return menuStats;
  }

  public void setMenuStats(List<MenuStat> menuStats) {
    this.menuStats = menuStats;
  }
}
