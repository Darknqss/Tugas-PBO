package Model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class Customer {

    private int id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private List<String> addresses;

    // Constructors, getters, and setters
    public Customer() {
    }

    public Customer(int id, String email, String firstName, String lastName, String phoneNumber) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }

    // Method to convert Customer object to JSON object
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("email", email);
        json.put("first_name", firstName);
        json.put("last_name", lastName);
        json.put("phone_number", phoneNumber);

        // Adding addresses if present
        if (addresses != null && !addresses.isEmpty()) {
            JSONArray addressesArray = new JSONArray(addresses);
            json.put("addresses", addressesArray);
        }

        return json;
    }
}
