package com.SubscriptionAPI.Handler;

import com.google.gson.Gson;
import com.SubscriptionAPI.database.DatabaseConnection;
import com.SubscriptionAPI.Model.Customer;
import com.SubscriptionAPI.Util.ErrorResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerHandler {

    private static final Gson gson = new Gson();

    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                handleGet(exchange);
                break;
            case "POST":
                handlePost(exchange);
                break;
            case "PUT":
                handlePut(exchange);
                break;
            case "DELETE":
                handleDelete(exchange);
                break;
            default:
                sendResponse(exchange, HttpURLConnection.HTTP_BAD_METHOD, "Method not allowed");
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.equals("/customers")) {
            getAllCustomers(exchange);
        } else if (path.startsWith("/customers/")) {
            String[] segments = path.split("/");
            String customerId = segments[segments.length - 1];
            getCustomerById(exchange, customerId);
        } else {
            sendResponse(exchange, HttpURLConnection.HTTP_NOT_FOUND, "Resource not found");
        }
    }

    private void getAllCustomers(HttpExchange exchange) throws IOException {
        List<Customer> customers = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String query = "SELECT * FROM customers";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Customer customer = new Customer(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("phone_number")
                );
                customers.add(customer);
            }
            sendJsonResponse(exchange, HttpURLConnection.HTTP_OK, customers);
        } catch (SQLException e) {
            handleException(e, exchange);
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    private void getCustomerById(HttpExchange exchange, String customerId) throws IOException {
        Customer customer = null;
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String query = "SELECT * FROM customers WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, Integer.parseInt(customerId));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                customer = new Customer(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("phone_number")
                );
                sendJsonResponse(exchange, HttpURLConnection.HTTP_OK, customer);
            } else {
                sendResponse(exchange, HttpURLConnection.HTTP_NOT_FOUND, "Customer with ID " + customerId + " not found.");
            }
        } catch (SQLException e) {
            handleException(e, exchange);
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());
        Customer newCustomer = gson.fromJson(body, Customer.class);
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String query = "INSERT INTO customers (email, first_name, last_name, phone_number) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, newCustomer.getEmail());
            stmt.setString(2, newCustomer.getFirstName());
            stmt.setString(3, newCustomer.getLastName());
            stmt.setString(4, newCustomer.getPhoneNumber());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating customer failed, no rows affected.");
            }
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                newCustomer.setId(generatedKeys.getInt(1));
                sendJsonResponse(exchange, HttpURLConnection.HTTP_CREATED, newCustomer);
            } else {
                throw new SQLException("Creating customer failed, no ID obtained.");
            }
        } catch (SQLException e) {
            handleException(e, exchange);
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    private void handlePut(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.startsWith("/customers/")) {
            String[] segments = path.split("/");
            String customerId = segments[segments.length - 1];
            updateCustomer(exchange, customerId);
        } else {
            sendResponse(exchange, HttpURLConnection.HTTP_NOT_FOUND, "Resource not found");
        }
    }

    private void updateCustomer(HttpExchange exchange, String customerId) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());
        Customer updatedCustomer = gson.fromJson(body, Customer.class);
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String query = "UPDATE customers SET email = ?, first_name = ?, last_name = ?, phone_number = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, updatedCustomer.getEmail());
            stmt.setString(2, updatedCustomer.getFirstName());
            stmt.setString(3, updatedCustomer.getLastName());
            stmt.setString(4, updatedCustomer.getPhoneNumber());
            stmt.setInt(5, Integer.parseInt(customerId));
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                sendJsonResponse(exchange, HttpURLConnection.HTTP_OK, updatedCustomer);
            } else {
                sendResponse(exchange, HttpURLConnection.HTTP_NOT_FOUND, "Customer with ID " + customerId + " not found.");
            }
        } catch (SQLException e) {
            handleException(e, exchange);
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.startsWith("/customers/")) {
            String[] segments = path.split("/");
            String customerId = segments[segments.length - 1];
            deleteCustomer(exchange, customerId);
        } else {
            sendResponse(exchange, HttpURLConnection.HTTP_NOT_FOUND, "Resource not found");
        }
    }

    private void deleteCustomer(HttpExchange exchange, String customerId) throws IOException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String query = "DELETE FROM customers WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, Integer.parseInt(customerId));
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                sendResponse(exchange, HttpURLConnection.HTTP_OK, "Customer deleted successfully");
            } else {
                sendResponse(exchange, HttpURLConnection.HTTP_NOT_FOUND, "Customer with ID " + customerId + " not found.");
            }
        } catch (SQLException e) {
            handleException(e, exchange);
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, Object object) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, 0);
        try (OutputStream os = exchange.getResponseBody()) {
            String jsonResponse = gson.toJson(object);
            os.write(jsonResponse.getBytes());
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        exchange.sendResponseHeaders(statusCode, message.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(message.getBytes());
        }
    }

    private void handleException(SQLException e, HttpExchange exchange) throws IOException {
        ErrorResponse errorResponse = new ErrorResponse("Internal Server Error: " + e.getMessage());
        sendJsonResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, errorResponse);
    }
}
