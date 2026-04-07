package dto;

import java.time.LocalDateTime;

public class Waiting {

    private int waitingId;
    private int customerId;
    private int storeId;
    private int waitingNumber;
    private int peopleCount;
    private String status;
    private LocalDateTime calledAt;
    private LocalDateTime createdAt;

    public Waiting() {}

    public Waiting(int waitingId, int customerId, int storeId, int waitingNumber, int peopleCount, String status, LocalDateTime calledAt, LocalDateTime createdAt) {
        this.waitingId = waitingId;
        this.customerId = customerId;
        this.storeId = storeId;
        this.waitingNumber = waitingNumber;
        this.peopleCount = peopleCount;
        this.status = status;
        this.calledAt = calledAt;
        this.createdAt = createdAt;
    }

    public int getWaitingId() { return waitingId; }
    public void setWaitingId(int waitingId) { this.waitingId = waitingId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getStoreId() { return storeId; }
    public void setStoreId(int storeId) { this.storeId = storeId; }

    public int getWaitingNumber() { return waitingNumber; }
    public void setWaitingNumber(int waitingNumber) { this.waitingNumber = waitingNumber; }

    public int getPeopleCount() { return peopleCount; }
    public void setPeopleCount(int peopleCount) { this.peopleCount = peopleCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCalledAt() { return calledAt; }
    public void setCalledAt(LocalDateTime calledAt) { this.calledAt = calledAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
