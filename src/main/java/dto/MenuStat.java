package dto;

public class MenuStat {
  private String menuName;
  private int orderCount;

  public MenuStat(String menuName, int orderCount) {
    this.menuName = menuName;
    this.orderCount = orderCount;
  }

  public String getMenuName() {
    return menuName;
  }

  public void setMenuName(String menuName) {
    this.menuName = menuName;
  }

  public int getOrderCount() {
    return orderCount;
  }

  public void setOrderCount(int orderCount) {
    this.orderCount = orderCount;
  }
}
