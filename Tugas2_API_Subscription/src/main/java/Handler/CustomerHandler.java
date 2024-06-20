package Handler;

import Model.Customer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import database.DatabaseConnection;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CustomerHandler implements HttpHandler {

    private final String apiKey;

    public CustomerHandler(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");

        // Verifikasi API key
        if (!apiKey.equals(authHeader)) {
            sendResponse(exchange, HttpURLConnection.HTTP_FORBIDDEN, "{\"error\":\"Forbidden\"}");
            return;
        }

        try {
            switch (method) {
                case "GET":
                    if (path.matches("/customers/?")) {
                        getAllCustomers(exchange);
                    } else if (path.matches("/customers/\\d+/?")) {
                        getCustomerById(exchange);
                    } else {
                        sendResponse(exchange, HttpURLConnection.HTTP_NOT_FOUND, "{\"error\":\"Endpoint not found\"}");
                    }
                    break;
                case "POST":
                    if (path.matches("/customers/?")) {
                        createCustomer(exchange);
                    } else {
                        sendResponse(exchange, HttpURLConnection.HTTP_NOT_FOUND, "{\"error\":\"Endpoint not found\"}");
                    }
                    break;
                case "PUT":
                    if (path.matches("/customers/\\d+/?")) {
                        updateCustomer(exchange);
                    } else {
                        sendResponse(exchange, HttpURLConnection.HTTP_NOT_FOUND, "{\"error\":\"Endpoint not found\"}");
                    }
                    break;
                case "DELETE":
                    if (path.matches("/customers/\\d+/?")) {
                        deleteCustomer(exchange);
                    } else {
                        sendResponse(exchange, HttpURLConnection.HTTP_NOT_FOUND, "{\"error\":\"Endpoint not found\"}");
                    }
                    break;
                default:
                    sendResponse(exchange, HttpURLConnection.HTTP_BAD_METHOD, "{\"error\":\"Method Not Allowed\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Internal Server Error\"}");
        }
    }

    private void getAllCustomers(HttpExchange exchange) throws IOException {
        List<Customer> customers = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM customers";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Customer customer = new Customer();
                    customer.setId(rs.getInt("id"));
                    customer.setEmail(rs.getString("email"));
                    customer.setFirstName(rs.getString("first_name"));
                    customer.setLastName(rs.getString("last_name"));
                    customer.setPhoneNumber(rs.getString("phone_number"));
                    customers.add(customer);
                }
            }
            sendResponse(exchange, HttpURLConnection.HTTP_OK, new JSONObject().put("customers", customers).toString());
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Could not retrieve customers\"}");
        }
    }

    private void getCustomerById(HttpExchange exchange) throws IOException {
        String[] parts = exchange.getRequestURI().getPath().split("/");
        int customerId = Integer.parseInt(parts[2]);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM customers WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, customerId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Customer customer = new Customer();
                        customer.setId(rs.getInt("id"));
                        customer.setEmail(rs.getString("email"));
                        customer.setFirstName(rs.getString("first_name"));
                        customer.setLastName(rs.getString("last_name"));
                        customer.setPhoneNumber(rs.getString("phone_number"));
                        sendResponse(exchange, HttpURLConnection.HTTP_OK, customer.toString());
                    } else {
                        sendResponse(exchange, HttpURLConnection.HTTP_NOT_FOUND, "{\"error\":\"Customer not found\"}");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Could not retrieve customer\"}");
        }
    }

    private void createCustomer(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        JSONObject json = new JSONObject(requestBody.toString());

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO customers (email, first_name, last_name, phone_number) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, json.getString("email"));
                stmt.setString(2, json.getString("first_name"));
                stmt.setString(3, json.getString("last_name"));
                stmt.setString(4, json.getString("phone_number"));
                stmt.executeUpdate();
                sendResponse(exchange, HttpURLConnection.HTTP_CREATED, "{\"message\":\"Customer created\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Could not create customer\"}");
        }
    }

    private void updateCustomer(HttpExchange exchange) throws IOException {
        String[] parts = exchange.getRequestURI().getPath().split("/");
        int customerId = Integer.parseInt(parts[2]);

        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        JSONObject json = new JSONObject(requestBody.toString());

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE customers SET email = ?, first_name = ?, last_name = ?, phone_number = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, json.getString("email"));
                stmt.setString(2, json.getString("first_name"));
                stmt.setString(3, json.getString("last_name"));
                stmt.setString(4, json.getString("phone_number"));
                stmt.setInt(5, customerId);
                int rowsUpdated = stmt.executeUpdate();

                if (rowsUpdated > 0) {
                    sendResponse(exchange, HttpURLConnection.HTTP_OK, "{\"message\":\"Customer updated\"}");
                } else {
                    sendResponse(exchange, HttpURLConnection.HTTP_NOT_FOUND, "{\"error\":\"Customer not found\"}");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Could not update customer\"}");
        }
    }

    private void deleteCustomer(HttpExchange exchange) throws IOException {
        String[] parts = exchange.getRequestURI().getPath().split("/");
        int customerId = Integer.parseInt(parts[2]);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM customers WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, customerId);
                int rowsDeleted = stmt.executeUpdate();

                if (rowsDeleted > 0) {
                    sendResponse(exchange, HttpURLConnection.HTTP_OK, "{\"message\":\"Customer deleted\"}");
                } else {
                    sendResponse(exchange, HttpURLConnection.HTTP_NOT_FOUND, "{\"error\":\"Customer not found\"}");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Could not delete customer\"}");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
