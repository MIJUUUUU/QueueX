package dto;

public class OrderItem {

    private int orderItemId;
    private int waitingId;
    private int menuId;
    private int quantity;

    public OrderItem() {}

    public OrderItem(int orderItemId, int waitingId, int menuId, int quantity) {
        this.orderItemId = orderItemId;
        this.waitingId = waitingId;
        this.menuId = menuId;
        this.quantity = quantity;
    }

    public int getOrderItemId() { return orderItemId; }
    public void setOrderItemId(int orderItemId) { this.orderItemId = orderItemId; }

    public int getWaitingId() { return waitingId; }
    public void setWaitingId(int waitingId) { this.waitingId = waitingId; }

    public int getMenuId() { return menuId; }
    public void setMenuId(int menuId) { this.menuId = menuId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
