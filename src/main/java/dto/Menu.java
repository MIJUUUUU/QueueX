package dto;

public class Menu {

    private int menuId;
    private int storeId;
    private String menuName;
    private int price;
    private boolean isAvailable;

    public Menu() {}

    public Menu(int menuId, int storeId, String menuName, int price, boolean isAvailable) {
        this.menuId = menuId;
        this.storeId = storeId;
        this.menuName = menuName;
        this.price = price;
        this.isAvailable = isAvailable;
    }

    public int getMenuId() { return menuId; }
    public void setMenuId(int menuId) { this.menuId = menuId; }

    public int getStoreId() { return storeId; }
    public void setStoreId(int storeId) { this.storeId = storeId; }

    public String getMenuName() { return menuName; }
    public void setMenuName(String menuName) { this.menuName = menuName; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
}
