package Model;

import java.sql.Timestamp;

public class Subscription {
    private int id;
    private int customerId;
    private String billingPeriod;
    private String billingPeriodUnit;
    private double totalDue;
    private Timestamp activatedAt;
    private Timestamp currentTermStart;
    private Timestamp currentTermEnd;
    private String status;

    // Constructors
    public Subscription() {}

    public Subscription(int id, int customerId, String billingPeriod, String billingPeriodUnit, double totalDue,
                        Timestamp activatedAt, Timestamp currentTermStart, Timestamp currentTermEnd, String status) {
        this.id = id;
        this.customerId = customerId;
        this.billingPeriod = billingPeriod;
        this.billingPeriodUnit = billingPeriodUnit;
        this.totalDue = totalDue;
        this.activatedAt = activatedAt;
        this.currentTermStart = currentTermStart;
        this.currentTermEnd = currentTermEnd;
        this.status = status;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getBillingPeriod() { return billingPeriod; }
    public void setBillingPeriod(String billingPeriod) { this.billingPeriod = billingPeriod; }

    public String getBillingPeriodUnit() { return billingPeriodUnit; }
    public void setBillingPeriodUnit(String billingPeriodUnit) { this.billingPeriodUnit = billingPeriodUnit; }

    public double getTotalDue() { return totalDue; }
    public void setTotalDue(double totalDue) { this.totalDue = totalDue; }

    public Timestamp getActivatedAt() { return activatedAt; }
    public void setActivatedAt(Timestamp activatedAt) { this.activatedAt = activatedAt; }

    public Timestamp getCurrentTermStart() { return currentTermStart; }
    public void setCurrentTermStart(Timestamp currentTermStart) { this.currentTermStart = currentTermStart; }

    public Timestamp getCurrentTermEnd() { return currentTermEnd; }
    public void setCurrentTermEnd(Timestamp currentTermEnd) { this.currentTermEnd = currentTermEnd; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Override toString() method to represent the object as a JSON-like string
    @Override
    public String toString() {
        return String.format("{\"id\":%d,\"customerId\":%d,\"billingPeriod\":\"%s\",\"billingPeriodUnit\":\"%s\",\"totalDue\":%f," +
                        "\"activatedAt\":\"%s\",\"currentTermStart\":\"%s\",\"currentTermEnd\":\"%s\",\"status\":\"%s\"}",
                id, customerId, billingPeriod, billingPeriodUnit, totalDue,
                activatedAt, currentTermStart, currentTermEnd, status);
    }
}
