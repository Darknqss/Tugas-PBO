package Model;

public class SubscriptionItem {
    private int itemId;
    private int quantity;
    private double price;
    private double amount;
    private Item item;

    public SubscriptionItem() {}

    public SubscriptionItem(int itemId, int quantity, double price, double amount) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.price = price;
        this.amount = amount;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "SubscriptionItem{" +
                "itemId=" + itemId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", amount=" + amount +
                ", item=" + item +
                '}';
    }
}
