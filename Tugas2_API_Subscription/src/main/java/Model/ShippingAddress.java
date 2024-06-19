package Model;

public class ShippingAddress {
    private int id;
    private int customerId;
    private String title;
    private String line1;
    private String line2;
    private String province;
    private String postcode;

    // Constructors
    public ShippingAddress() {}

    public ShippingAddress(int id, int customerId, String title, String line1, String line2, String province, String postcode) {
        this.id = id;
        this.customerId = customerId;
        this.title = title;
        this.line1 = line1;
        this.line2 = line2;
        this.province = province;
        this.postcode = postcode;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getLine1() { return line1; }
    public void setLine1(String line1) { this.line1 = line1; }

    public String getLine2() { return line2; }
    public void setLine2(String line2) { this.line2 = line2; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getPostcode() { return postcode; }
    public void setPostcode(String postcode) { this.postcode = postcode; }

    // Override toString() method to represent the object as a JSON-like string
    @Override
    public String toString() {
        return String.format("{\"id\":%d,\"customerId\":%d,\"title\":\"%s\",\"line1\":\"%s\",\"line2\":\"%s\",\"province\":\"%s\",\"postcode\":\"%s\"}",
                id, customerId, title, line1, line2, province, postcode);
    }
}

