package com.TugasPBO1;

public class Login {

    // Deklarasi Field
    private String customer_username, customer_password, admin_username, admin_password;

    // Konstruktor
    public Login(){

        // Customer
        customer_username = "customer";
        customer_password = "pwcustomer";

        // Admin
        admin_username = "admin";
        admin_password = "pwadmin";
    }

    // Setter
    public void setCustomer_username(String customer_username) {
        this.customer_username = customer_username;
    }
    public void setCustomer_password(String customer_password) {
        this.customer_password = customer_password;
    }
    public void setAdmin_username(String admin_username) {
        this.admin_username = admin_username;
    }
    public void setAdmin_password(String admin_password) {
        this.admin_password = admin_password;
    }

    // Getter
    public String getCustomer_username() {
        return customer_username;
    }
    public String getCustomer_password() {
        return customer_password;
    }
    public String getAdmin_username() {
        return admin_username;
    }
    public String getAdmin_password() {
        return admin_password;
    }

}
