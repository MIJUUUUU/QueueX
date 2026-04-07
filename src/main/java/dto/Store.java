package dto;

public class Store {
  private int storeId;
  private int adminId;
  private String storeName;
  private String category;

  public Store() {
  }

  public Store(int storeId, int adminId, String storeName, String category) {
    this.storeId = storeId;
    this.adminId = adminId;
    this.storeName = storeName;
    this.category = category;
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
}