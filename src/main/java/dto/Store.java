package dto;

public class Store {
  private int storeId;
  private int adminId;
  private String storeName;
  private String category;
  private int maxCapacity;
  private int maxGroupSize;

  public Store() {
  }

  public Store(int storeId, int adminId, String storeName, String category, int maxCapacity, int maxGroupSize) {
    this.storeId = storeId;
    this.adminId = adminId;
    this.storeName = storeName;
    this.category = category;
    this.maxCapacity = maxCapacity;
    this.maxGroupSize = maxGroupSize;
  }

  public int getStoreId() {
    return storeId;
  }

  public void setStoreId(int storeId) {
    this.storeId = storeId;
  }

  public int getAdminId() {
    return adminId;
  }

  public void setAdminId(int adminId) {
    this.adminId = adminId;
  }

  public String getStoreName() {
    return storeName;
  }

  public void setStoreName(String storeName) {
    this.storeName = storeName;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public int getMaxCapacity() {
    return maxCapacity;
  }

  public void setMaxCapacity(int maxCapacity) {
    this.maxCapacity = maxCapacity;
  }

  public int getMaxGroupSize() {
    return maxGroupSize;
  }

  public void setMaxGroupSize(int maxGroupSize) {
    this.maxGroupSize = maxGroupSize;
  }
}
