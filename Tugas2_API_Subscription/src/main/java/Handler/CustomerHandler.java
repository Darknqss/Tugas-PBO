package Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import database.DatabaseConnection;
import Model.Customer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class CustomerHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String apiKey = exchange.getRequestHeaders().getFirst("API-Key");

        if (!"hardcoded-api-key".equals(apiKey)) {
            sendResponse(exchange, 403, "{\"error\":\"Forbidden\"}");
            return;
        }

        try {
            if ("GET".equalsIgnoreCase(method)) {
                if (path.matches("/customers/?")) {
                    getAllCustomers(exchange);
                } else if (path.matches("/customers/\\d+/?")) {
                    getCustomerById(exchange);
                }
            } else if ("POST".equalsIgnoreCase(method)) {
                if (path.matches("/customers/?")) {
                    createCustomer(exchange);
                }
            } else if ("PUT".equalsIgnoreCase(method)) {
                if (path.matches("/customers/\\d+/?")) {
                    updateCustomer(exchange);
                }
            } else if ("DELETE".equalsIgnoreCase(method)) {
                // Implement delete if necessary
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"Internal Server Error\"}");
        }
    }

    private void getAllCustomers(HttpExchange exchange) throws IOException {
        List<Customer> customers = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM customers";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Customer customer = new Customer();
                customer.setId(rs.getInt("id"));
                customer.setEmail(rs.getString("email"));
                customer.setFirstName(rs.getString("first_name"));
                customer.setLastName(rs.getString("last_name"));
                customer.setPhoneNumber(rs.getString("phone_number"));
                customers.add(customer);
            }

            sendResponse(exchange, 200, customers.toString());
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"Could not retrieve customers\"}");
        }
    }

    private void getCustomerById(HttpExchange exchange) throws IOException {
        String[] parts = exchange.getRequestURI().getPath().split("/");
        int customerId = Integer.parseInt(parts[2]);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM customers WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Customer customer = new Customer();
                customer.setId(rs.getInt("id"));
                customer.setEmail(rs.getString("email"));
                customer.setFirstName(rs.getString("first_name"));
                customer.setLastName(rs.getString("last_name"));
                customer.setPhoneNumber(rs.getString("phone_number"));

                sendResponse(exchange, 200, customer.toString());
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Customer not found\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"Could not retrieve customer\"}");
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
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, json.getString("email"));
            stmt.setString(2, json.getString("first_name"));
            stmt.setString(3, json.getString("last_name"));
            stmt.setString(4, json.getString("phone_number"));
            stmt.executeUpdate();

            sendResponse(exchange, 201, "{\"message\":\"Customer created\"}");
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"Could not create customer\"}");
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
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, json.getString("email"));
            stmt.setString(2, json.getString("first_name"));
            stmt.setString(3, json.getString("last_name"));
            stmt.setString(4, json.getString("phone_number"));
            stmt.setInt(5, customerId);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                sendResponse(exchange, 200, "{\"message\":\"Customer updated\"}");
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Customer not found\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"Could not update customer\"}");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}

