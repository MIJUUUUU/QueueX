package dto;

//객체
public class Customer {
    private int customerId;
    private String phone;
    private String password;

    public Customer() {}

    public Customer(int customerId, String phone, String password) {
        this.customerId = customerId;
        this.phone = phone;
        this.password = password;
    }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
