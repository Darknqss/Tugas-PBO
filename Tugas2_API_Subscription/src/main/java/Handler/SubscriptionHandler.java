package Handler;

import Model.Item;
import Model.Customer;
import Model.Subscription;
import Model.SubscriptionItem;
import ErrorHandler.ErrorResponse;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import database.DatabaseConnection;
import org.json.JSONArray;
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

        // API key verification
        if (authHeader == null || !authHeader.equals(apiKey)) {
            sendResponse(exchange, 403, ErrorResponse.create("Forbidden"));
            return;
        }

        try {
            switch (method.toUpperCase()) {
                case "GET":
                    handleGetRequest(exchange, path);
                    break;
                case "POST":
                    handlePostRequest(exchange, path);
                    break;
                default:
                    sendResponse(exchange, 405, ErrorResponse.create("Method Not Allowed"));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, ErrorResponse.create("Internal Server Error"));
        }
    }

    private void handleGetRequest(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/subscriptions/?")) {
            if (exchange.getRequestURI().getQuery() != null &&
                    exchange.getRequestURI().getQuery().contains("sort_by=current_term_end&sort_type=desc")) {
                getAllSubscriptionsSortedByCurrentTermEndDesc(exchange);
            } else {
                getAllSubscriptions(exchange);
            }
        } else if (path.matches("/subscriptions/\\d+/?")) {
            getSubscriptionById(exchange);
        } else {
            sendResponse(exchange, 404, ErrorResponse.create("Endpoint not found"));
        }
    }

    private void handlePostRequest(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/subscriptions/?")) {
            createSubscription(exchange);
        } else {
            sendResponse(exchange, 404, ErrorResponse.create("Endpoint not found"));
        }
    }

    private void getAllSubscriptions(HttpExchange exchange) throws IOException {
        List<Subscription> subscriptions = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM subscriptions");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Subscription subscription = mapResultSetToSubscription(rs);
                subscription.setItems(getSubscriptionItems(conn, subscription.getId()));
                subscriptions.add(subscription);
            }

            sendResponse(exchange, 200, serializeSubscriptionsToJson(subscriptions).toString());
        } catch (SQLException e) {
            e.printStackTrace();
            sendResponse(exchange, 500, ErrorResponse.create("Could not retrieve subscriptions"));
        }
    }

    private void getAllSubscriptionsSortedByCurrentTermEndDesc(HttpExchange exchange) throws IOException {
        List<Subscription> subscriptions = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM subscriptions ORDER BY current_term_end DESC");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Subscription subscription = mapResultSetToSubscription(rs);
                subscription.setItems(getSubscriptionItems(conn, subscription.getId()));
                subscriptions.add(subscription);
            }

            sendResponse(exchange, 200, serializeSubscriptionsToJson(subscriptions).toString());
        } catch (SQLException e) {
            e.printStackTrace();
            sendResponse(exchange, 500, ErrorResponse.create("Could not retrieve subscriptions"));
        }
    }

    private Subscription mapResultSetToSubscription(ResultSet rs) throws SQLException {
        Subscription subscription = new Subscription();
        subscription.setId(rs.getInt("id"));
        subscription.setCustomerId(rs.getInt("customer_id"));
        subscription.setBillingPeriod(String.valueOf(rs.getInt("billing_period")));
        subscription.setBillingPeriodUnit(rs.getString("billing_period_unit"));
        subscription.setTotalDue(rs.getDouble("total_due"));
        subscription.setActivatedAt(rs.getTimestamp("activated_at"));
        subscription.setCurrentTermStart(rs.getTimestamp("current_term_start"));
        subscription.setCurrentTermEnd(rs.getTimestamp("current_term_end"));
        subscription.setStatus(rs.getString("status"));
        return subscription;
    }

    private List<SubscriptionItem> getSubscriptionItems(Connection conn, int subscriptionId) throws SQLException {
        List<SubscriptionItem> items = new ArrayList<>();
        String sql = "SELECT si.*, i.name, i.price, i.type FROM subscription_items si " +
                "JOIN items i ON si.item_id = i.id " +
                "WHERE subscription_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, subscriptionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SubscriptionItem item = new SubscriptionItem();
                    item.setItemId(rs.getInt("item_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setAmount(rs.getDouble("amount"));

                    Item subscriptionItem = new Item();
                    subscriptionItem.setId(rs.getInt("item_id"));
                    subscriptionItem.setName(rs.getString("name"));
                    subscriptionItem.setPrice(rs.getDouble("price"));
                    subscriptionItem.setType(rs.getString("type"));

                    item.setItem(subscriptionItem);

                    items.add(item);
                }
            }
        }
        return items;
    }

    private JSONObject serializeSubscriptionsToJson(List<Subscription> subscriptions) {
        JSONObject result = new JSONObject();
        JSONArray subscriptionsArray = new JSONArray();

        for (Subscription subscription : subscriptions) {
            JSONObject subscriptionJson = new JSONObject();
            subscriptionJson.put("id", subscription.getId());
            subscriptionJson.put("customer_id", subscription.getCustomerId());
            subscriptionJson.put("billing_period", subscription.getBillingPeriod());
            subscriptionJson.put("billing_period_unit", subscription.getBillingPeriodUnit());
            subscriptionJson.put("total_due", subscription.getTotalDue());
            subscriptionJson.put("activated_at", subscription.getActivatedAt().toString());
            subscriptionJson.put("current_term_start", subscription.getCurrentTermStart().toString());
            subscriptionJson.put("current_term_end", subscription.getCurrentTermEnd().toString());
            subscriptionJson.put("status", subscription.getStatus());

            JSONArray itemsArray = new JSONArray();
            for (SubscriptionItem item : subscription.getItems()) {
                JSONObject itemJson = new JSONObject();
                itemJson.put("item_id", item.getItem().getId());
                itemJson.put("name", item.getItem().getName());
                itemJson.put("price", item.getItem().getPrice());
                itemJson.put("type", item.getItem().getType());
                itemJson.put("quantity", item.getQuantity());
                itemJson.put("amount", item.getAmount());
                itemsArray.put(itemJson);
            }

            subscriptionJson.put("items", itemsArray);
            subscriptionsArray.put(subscriptionJson);
        }

        result.put("subscriptions", subscriptionsArray);
        return result;
    }

    private void getSubscriptionById(HttpExchange exchange) throws IOException {
        String[] parts = exchange.getRequestURI().getPath().split("/");
        int subscriptionId;
        try {
            subscriptionId = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, ErrorResponse.create("Invalid subscription ID"));
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT s.*, c.first_name, c.last_name FROM subscriptions s " +
                    "JOIN customers c ON s.customer_id = c.id " +
                    "WHERE s.id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, subscriptionId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Subscription subscription = mapResultSetToSubscription(rs);
                        subscription.setItems(getSubscriptionItems(conn, subscriptionId));

                        // Create Customer object and set its properties
                        Customer customer = new Customer();
                        customer.setId(rs.getInt("customer_id"));
                        customer.setFirstName(rs.getString("first_name"));
                        customer.setLastName(rs.getString("last_name"));

                        JSONObject subscriptionJson = serializeSubscriptionToJson(subscription, customer);
                        sendResponse(exchange, 200, subscriptionJson.toString());
                    } else {
                        sendResponse(exchange, 404, ErrorResponse.create("Subscription not found"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sendResponse(exchange, 500, ErrorResponse.create("Could not retrieve subscription"));
        }
    }

    private JSONObject serializeSubscriptionToJson(Subscription subscription, Customer customer) {
        JSONObject subscriptionJson = new JSONObject();
        subscriptionJson.put("id", subscription.getId());
        subscriptionJson.put("customer_id", customer.getId());
        subscriptionJson.put("first_name", customer.getFirstName());
        subscriptionJson.put("last_name", customer.getLastName());
        subscriptionJson.put("billing_period", subscription.getBillingPeriod());
        subscriptionJson.put("billing_period_unit", subscription.getBillingPeriodUnit());
        subscriptionJson.put("total_due", subscription.getTotalDue());
        subscriptionJson.put("activated_at", subscription.getActivatedAt().toString());
        subscriptionJson.put("current_term_start", subscription.getCurrentTermStart().toString());
        subscriptionJson.put("current_term_end", subscription.getCurrentTermEnd().toString());
        subscriptionJson.put("status", subscription.getStatus());

        JSONArray itemsArray = new JSONArray();
        for (SubscriptionItem item : subscription.getItems()) {
            JSONObject itemJson = new JSONObject();
            itemJson.put("item_id", item.getItem().getId());
            itemJson.put("name", item.getItem().getName());
            itemJson.put("price", item.getItem().getPrice());
            itemJson.put("type", item.getItem().getType());
            itemJson.put("quantity", item.getQuantity());
            itemJson.put("amount", item.getAmount());
            itemsArray.put(itemJson);
        }

        subscriptionJson.put("items", itemsArray);
        return subscriptionJson;
    }

    private void createSubscription(HttpExchange exchange) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }

        JSONObject json;
        try {
            json = new JSONObject(requestBody.toString());
        } catch (Exception e) {
            sendResponse(exchange, 400, ErrorResponse.create("Invalid JSON input"));
            return;
        }

        // Extract data from JSON
        int customerId = json.getInt("customer_id");
        String billingPeriod = json.getString("billing_period");
        String billingPeriodUnit = json.getString("billing_period_unit");
        double totalDue = json.getDouble("total_due");
        String activatedAtStr = json.getString("activated_at");
        String currentTermStartStr = json.getString("current_term_start");
        String currentTermEndStr = json.getString("current_term_end");
        String status = json.getString("status");
        JSONArray itemsArray = json.getJSONArray("items");

        // Convert timestamp strings to Timestamp objects
        Timestamp activatedAt = Timestamp.valueOf(activatedAtStr.replace("T", " ").replace("Z", ""));
        Timestamp currentTermStart = Timestamp.valueOf(currentTermStartStr.replace("T", " ").replace("Z", ""));
        Timestamp currentTermEnd = Timestamp.valueOf(currentTermEndStr.replace("T", " ").replace("Z", ""));

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if the customer_id exists in the customers table
            String customerCheckSql = "SELECT COUNT(*) FROM customers WHERE id = ?";
            try (PreparedStatement customerCheckStmt = conn.prepareStatement(customerCheckSql)) {
                customerCheckStmt.setInt(1, customerId);
                try (ResultSet rs = customerCheckStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        sendResponse(exchange, 400, ErrorResponse.create("Invalid customer_id: Customer does not exist"));
                        return;
                    }
                }
            }

            // Insert into subscriptions table
            String sql = "INSERT INTO subscriptions (customer_id, billing_period, billing_period_unit, total_due, activated_at, current_term_start, current_term_end, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, customerId);
                stmt.setString(2, billingPeriod);
                stmt.setString(3, billingPeriodUnit);
                stmt.setDouble(4, totalDue);
                stmt.setTimestamp(5, activatedAt);
                stmt.setTimestamp(6, currentTermStart);
                stmt.setTimestamp(7, currentTermEnd);
                stmt.setString(8, status);
                stmt.executeUpdate();

                // Retrieve auto-generated subscription ID
                int subscriptionId;
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        subscriptionId = generatedKeys.getInt(1);

                        // Insert into subscription_items table
                        insertSubscriptionItems(conn, subscriptionId, itemsArray);

                        // Construct response JSON
                        JSONObject responseJson = new JSONObject();
                        responseJson.put("message", "Subscription created with id " + subscriptionId);
                        sendResponse(exchange, 201, responseJson.toString());
                    } else {
                        sendResponse(exchange, 500, ErrorResponse.create("Failed to create subscription, no ID obtained."));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sendResponse(exchange, 500, ErrorResponse.create("Could not create subscription"));
        }
    }

    private void insertSubscriptionItems(Connection conn, int subscriptionId, JSONArray itemsArray) throws SQLException {
        String sql = "INSERT INTO subscription_items (subscription_id, item_id, quantity, amount, price) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject item = itemsArray.getJSONObject(i);
                stmt.setInt(1, subscriptionId);
                stmt.setInt(2, item.getInt("item_id"));
                stmt.setInt(3, item.getInt("quantity"));
                stmt.setDouble(4, item.getDouble("amount"));
                stmt.setDouble(5, item.getDouble("price"));

                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
