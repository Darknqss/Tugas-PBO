package Model;

public class Item {
    private int id;
    private String name;
    private double price;
    private String type;
    private boolean isActive;

    // Constructors
    public Item() {}

    public Item(int id, String name, double price, String type, boolean isActive) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.type = type;
        this.isActive = isActive;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    // Override toString() method to represent the object as a JSON-like string
    @Override
    public String toString() {
        return String.format("{\"id\":%d,\"name\":\"%s\",\"price\":%f,\"type\":\"%s\",\"isActive\":%b}",
                id, name, price, type, isActive);
    }
}
