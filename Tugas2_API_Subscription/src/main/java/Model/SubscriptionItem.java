package Model;

public class SubscriptionItem {
    private int subscriptionId;
    private int itemId;
    private int quantity;
    private double price;
    private double amount;

    // Constructors
    public SubscriptionItem() {}

    public SubscriptionItem(int subscriptionId, int itemId, int quantity, double price, double amount) {
        this.subscriptionId = subscriptionId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.price = price;
        this.amount = amount;
    }

    // Getters and setters
    public int getSubscriptionId() { return subscriptionId; }
    public void setSubscriptionId(int subscriptionId) { this.subscriptionId = subscriptionId; }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    // Override toString() method to represent the object as a JSON-like string
    @Override
    public String toString() {
        return String.format("{\"subscriptionId\":%d,\"itemId\":%d,\"quantity\":%d,\"price\":%f,\"amount\":%f}",
                subscriptionId, itemId, quantity, price, amount);
    }
}