package Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import database.DatabaseConnection;
import Model.Item;
import Util.ErrorResponse;
import org.json.JSONObject;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemHandler implements HttpHandler {

    private final String apiKey;

    public ItemHandler(String apiKey) {
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
                if (path.matches("/items/?")) {
                    if (exchange.getRequestURI().getQuery() != null && exchange.getRequestURI().getQuery().contains("is_active=true")) {
                        getAllActiveItems(exchange);
                    } else {
                        getAllItems(exchange);
                    }
                } else if (path.matches("/items/\\d+/?")) {
                    getItemById(exchange);
                } else {
                    sendResponse(exchange, 404, ErrorResponse.create("Endpoint not found"));
                }
            } else if ("POST".equalsIgnoreCase(method)) {
                if (path.matches("/items/?")) {
                    createItem(exchange);
                } else {
                    sendResponse(exchange, 404, ErrorResponse.create("Endpoint not found"));
                }
            } else if ("PUT".equalsIgnoreCase(method)) {
                if (path.matches("/items/\\d+/?")) {
                    updateItem(exchange);
                } else {
                    sendResponse(exchange, 404, ErrorResponse.create("Endpoint not found"));
                }
            } else if ("DELETE".equalsIgnoreCase(method)) {
                if (path.matches("/items/\\d+/?")) {
                    deleteItem(exchange);
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

    private void getAllItems(HttpExchange exchange) throws IOException {
        List<Item> items = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM items";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Item item = new Item();
                item.setId(rs.getInt("id"));
                item.setName(rs.getString("name"));
                item.setPrice(rs.getDouble("price"));
                item.setType(rs.getString("type"));
                item.setActive(rs.getBoolean("is_active"));
                items.add(item);
            }

            sendResponse(exchange, 200, new JSONObject().put("items", items).toString());
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, ErrorResponse.create("Could not retrieve items"));
        }
    }

    private void getAllActiveItems(HttpExchange exchange) throws IOException {
        List<Item> items = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM items WHERE is_active = true";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Item item = new Item();
                item.setId(rs.getInt("id"));
                item.setName(rs.getString("name"));
                item.setPrice(rs.getDouble("price"));
                item.setType(rs.getString("type"));
                item.setActive(rs.getBoolean("is_active"));
                items.add(item);
            }

            sendResponse(exchange, 200, new JSONObject().put("items", items).toString());
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, ErrorResponse.create("Could not retrieve active items"));
        }
    }

    private void getItemById(HttpExchange exchange) throws IOException {
        String[] parts = exchange.getRequestURI().getPath().split("/");
        int itemId = Integer.parseInt(parts[2]);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM items WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, itemId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Item item = new Item();
                item.setId(rs.getInt("id"));
                item.setName(rs.getString("name"));
                item.setPrice(rs.getDouble("price"));
                item.setType(rs.getString("type"));
                item.setActive(rs.getBoolean("is_active"));

                sendResponse(exchange, 200, item.toString());
            } else {
                sendResponse(exchange, 404, ErrorResponse.create("Item not found"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, ErrorResponse.create("Could not retrieve item"));
        }
    }

    private void createItem(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        JSONObject json = new JSONObject(requestBody.toString());

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO items (name, price, type, is_active) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, json.getString("name"));
            stmt.setDouble(2, json.getDouble("price"));
            stmt.setString(3, json.getString("type"));
            stmt.setBoolean(4, json.getBoolean("is_active"));
            stmt.executeUpdate();

            // Retrieve the auto-generated ID of the new item
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            int newItemId;
            if (generatedKeys.next()) {
                newItemId = generatedKeys.getInt(1);
                sendResponse(exchange, 201, new JSONObject().put("id", newItemId).put("message", "Item created").toString());
            } else {
                sendResponse(exchange, 500, ErrorResponse.create("Could not retrieve generated ID for new item"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, ErrorResponse.create("Could not create item"));
        }
    }

    private void updateItem(HttpExchange exchange) throws IOException {
        String[] parts = exchange.getRequestURI().getPath().split("/");
        int itemId = Integer.parseInt(parts[2]);

        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        JSONObject json = new JSONObject(requestBody.toString());

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE items SET name = ?, price = ?, type = ?, is_active = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, json.getString("name"));
            stmt.setDouble(2, json.getDouble("price"));
            stmt.setString(3, json.getString("type"));
            stmt.setBoolean(4, json.getBoolean("is_active"));
            stmt.setInt(5, itemId);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                sendResponse(exchange, 200, "{\"message\":\"Item updated\"}");
            } else {
                sendResponse(exchange, 404, ErrorResponse.create("Item not found"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, ErrorResponse.create("Could not update item"));
        }
    }

    private void deleteItem(HttpExchange exchange) throws IOException {
        String[] parts = exchange.getRequestURI().getPath().split("/");
        int itemId = Integer.parseInt(parts[2]);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE items SET is_active = false WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, itemId);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                sendResponse(exchange, 200, "{\"message\":\"Item deactivated\"}");
            } else {
                sendResponse(exchange, 404, ErrorResponse.create("Item not found"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, ErrorResponse.create("Could not deactivate item"));
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
