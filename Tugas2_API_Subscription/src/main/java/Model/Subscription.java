package Model;

import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.List;

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
    private List<SubscriptionItem> items; // List of SubscriptionItem

    // Constructors
    public Subscription() {}

    public Subscription(int id, int customerId, String billingPeriod, String billingPeriodUnit, double totalDue,
                        Timestamp activatedAt, Timestamp currentTermStart, Timestamp currentTermEnd, String status,
                        List<SubscriptionItem> items) {
        this.id = id;
        this.customerId = customerId;
        this.billingPeriod = billingPeriod;
        this.billingPeriodUnit = billingPeriodUnit;
        this.totalDue = totalDue;
        this.activatedAt = activatedAt;
        this.currentTermStart = currentTermStart;
        this.currentTermEnd = currentTermEnd;
        this.status = status;
        this.items = items;
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

    public List<SubscriptionItem> getItems() { return items; }
    public void setItems(List<SubscriptionItem> items) { this.items = items; }

    // Override toString() method to represent the object as JSON
    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("customerId", customerId);
        jsonObject.put("billingPeriod", billingPeriod);
        jsonObject.put("billingPeriodUnit", billingPeriodUnit);
        jsonObject.put("totalDue", totalDue);
        jsonObject.put("activatedAt", activatedAt != null ? activatedAt.toString() : null);
        jsonObject.put("currentTermStart", currentTermStart != null ? currentTermStart.toString() : null);
        jsonObject.put("currentTermEnd", currentTermEnd != null ? currentTermEnd.toString() : null);
        jsonObject.put("status", status);
        jsonObject.put("items", items); // This will use the toString() method of SubscriptionItem if overridden
        return jsonObject.toString();
    }
}
