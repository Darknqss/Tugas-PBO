package Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import database.DatabaseConnection;
import Model.Subscription;
import Util.ErrorResponse;
import org.json.JSONObject;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionHandler implements HttpHandler {

    private final String apiKey;

    public SubscriptionHandler(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");

        // Verifikasi API key
        if (!(apiKey).equals(authHeader)) {
            sendResponse(exchange, 403, ErrorResponse.create("Forbidden"));
            return;
        }

        try {
            if ("GET".equalsIgnoreCase(method)) {
                if (path.matches("/subscriptions/?")) {
                    getAllSubscriptions(exchange);
                } else if (path.matches("/subscriptions/\\d+/?")) {
                    getSubscriptionById(exchange);
                } else {
                    sendResponse(exchange, 404, ErrorResponse.create("Endpoint not found"));
                }
            } else if ("POST".equalsIgnoreCase(method)) {
                if (path.matches("/subscriptions/?")) {
                    createSubscription(exchange);
                } else {
                    sendResponse(exchange, 404, ErrorResponse.create("Endpoint not found"));
                }
            } else if ("PUT".equalsIgnoreCase(method)) {
                if (path.matches("/subscriptions/\\d+/?")) {
                    updateSubscription(exchange);
                } else {
                    sendResponse(exchange, 404, ErrorResponse.create("Endpoint not found"));
                }
            } else {
                sendResponse(exchange, 405, ErrorResponse.create("Method Not Allowed"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, ErrorResponse.create("Internal Server Error"));
        }
    }

    private void getAllSubscriptions(HttpExchange exchange) throws IOException {
        List<Subscription> subscriptions = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM subscriptions";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Subscription subscription = new Subscription();
                subscription.setId(rs.getInt("id"));
                subscription.setCustomerId(rs.getInt("customer_id"));
                subscription.setBillingPeriod(rs.getString("billing_period"));
                subscription.setBillingPeriodUnit(rs.getString("billing_period_unit"));
                subscription.setTotalDue(rs.getDouble("total_due"));
                subscription.setActivatedAt(rs.getTimestamp("activated_at"));
                subscription.setCurrentTermStart(rs.getTimestamp("current_term_start"));
                subscription.setCurrentTermEnd(rs.getTimestamp("current_term_end"));
                subscription.setStatus(rs.getString("status"));
                subscriptions.add(subscription);
            }

            sendResponse(exchange, 200, new JSONObject().put("subscriptions", subscriptions).toString());
        } catch (SQLException e) {
            e.printStackTrace();
            sendResponse(exchange, 500, ErrorResponse.create("Could not retrieve subscriptions"));
        }
    }

    private void getSubscriptionById(HttpExchange exchange) throws IOException {
        String[] parts = exchange.getRequestURI().getPath().split("/");
        int subscriptionId = Integer.parseInt(parts[2]);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM subscriptions WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, subscriptionId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Subscription subscription = new Subscription();
                subscription.setId(rs.getInt("id"));
                subscription.setCustomerId(rs.getInt("customer_id"));
                subscription.setBillingPeriod(rs.getString("billing_period"));
                subscription.setBillingPeriodUnit(rs.getString("billing_period_unit"));
                subscription.setTotalDue(rs.getDouble("total_due"));
                subscription.setActivatedAt(rs.getTimestamp("activated_at"));
                subscription.setCurrentTermStart(rs.getTimestamp("current_term_start"));
                subscription.setCurrentTermEnd(rs.getTimestamp("current_term_end"));
                subscription.setStatus(rs.getString("status"));

                sendResponse(exchange, 200, subscription.toString());
            } else {
                sendResponse(exchange, 404, ErrorResponse.create("Subscription not found"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sendResponse(exchange, 500, ErrorResponse.create("Could not retrieve subscription"));
        }
    }

    private void createSubscription(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        JSONObject json = new JSONObject(requestBody.toString());

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO subscriptions (customer_id, billing_period, billing_period_unit, total_due, activated_at, current_term_start, current_term_end, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, json.getInt("customer_id"));
            stmt.setString(2, json.getString("billing_period"));
            stmt.setString(3, json.getString("billing_period_unit"));
            stmt.setDouble(4, json.getDouble("total_due"));
            stmt.setTimestamp(5, Timestamp.valueOf(json.getString("activated_at")));
            stmt.setTimestamp(6, Timestamp.valueOf(json.getString("current_term_start")));
            stmt.setTimestamp(7, Timestamp.valueOf(json.getString("current_term_end")));
            stmt.setString(8, json.getString("status"));
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int subscriptionId = generatedKeys.getInt(1);
                sendResponse(exchange, 201, "{\"message\":\"Subscription created with id " + subscriptionId + "\"}");
            } else {
                sendResponse(exchange, 500, ErrorResponse.create("Failed to create subscription, no ID obtained."));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sendResponse(exchange, 500, ErrorResponse.create("Could not create subscription"));
        }
    }

    private void updateSubscription(HttpExchange exchange) throws IOException {
        String[] parts = exchange.getRequestURI().getPath().split("/");
        int subscriptionId = Integer.parseInt(parts[2]);

        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        JSONObject json = new JSONObject(requestBody.toString());

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE subscriptions SET billing_period = ?, billing_period_unit = ?, total_due = ?, activated_at = ?, current_term_start = ?, current_term_end = ?, status = ? " +
                    "WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, json.getString("billing_period"));
            stmt.setString(2, json.getString("billing_period_unit"));
            stmt.setDouble(3, json.getDouble("total_due"));
            stmt.setTimestamp(4, Timestamp.valueOf(json.getString("activated_at")));
            stmt.setTimestamp(5, Timestamp.valueOf(json.getString("current_term_start")));
            stmt.setTimestamp(6, Timestamp.valueOf(json.getString("current_term_end")));
            stmt.setString(7, json.getString("status"));
            stmt.setInt(8, subscriptionId);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                sendResponse(exchange, 200, "{\"message\":\"Subscription updated\"}");
            } else {
                sendResponse(exchange, 404, ErrorResponse.create("Subscription not found"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sendResponse(exchange, 500, ErrorResponse.create("Could not update subscription"));
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
