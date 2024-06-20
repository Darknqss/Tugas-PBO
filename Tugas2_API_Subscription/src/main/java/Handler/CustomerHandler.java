package Handler;

import Model.Customer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import database.DatabaseConnection;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// Handler untuk menangani permintaan HTTP yang berkaitan dengan entitas "Customer"
public class CustomerHandler implements HttpHandler {

    // API key yang digunakan untuk autentikasi
    private final String apiKey;

    // Konstruktor yang menerima API key
    public CustomerHandler(String apiKey) {
        this.apiKey = apiKey;
    }

    // Metode yang menangani semua permintaan HTTP ke endpoint /customers
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Mendapatkan metode HTTP (GET, POST, PUT, DELETE)
        String method = exchange.getRequestMethod();

        // Mendapatkan path dari URL yang diminta
        String path = exchange.getRequestURI().getPath();

        // Mendapatkan header Authorization untuk verifikasi API key
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");

        // Verifikasi API key
        if (!apiKey.equals(authHeader)) {
            // Jika API key tidak cocok, kirim respons HTTP 403 (Forbidden)
            sendResponse(exchange, HttpURLConnection.HTTP_FORBIDDEN, "{\"error\":\"Forbidden\"}");
            return;
        }

        try {
            // Switch case berdasarkan metode HTTP yang digunakan
            switch (method) {
                case "GET":
                    if (path.matches("/customers/?")) {
                        // Memproses permintaan GET untuk semua pelanggan
                        getAllCustomers(exchange);
                    } else if (path.matches("/customers/\\d+/?")) {
                        // Memproses permintaan GET untuk pelanggan berdasarkan ID
                        getCustomerById(exchange);
                    } else if (path.matches("/customers/\\d+/cards/?")) {
                        // Memproses permintaan GET untuk daftar kartu kredit/debit pelanggan
                        getCustomerCards(exchange);
                    } else if (path.matches("/customers/\\d+/subscriptions/?")) {
                        // Memproses permintaan GET untuk daftar subscriptions pelanggan
                        getCustomerSubscriptions(exchange);
                    } else {
                        // Jika endpoint tidak ditemukan, kirim respons HTTP 404 (Not Found)
                        sendResponse(exchange, HttpURLConnection.HTTP_NOT_FOUND, "{\"error\":\"Endpoint not found\"}");
                    }
                    break;
                case "POST":
                    if (path.matches("/customers/?")) {
                        addCustomers(exchange);
                    } else {
                        sendResponse(exchange, HttpURLConnection.HTTP_NOT_FOUND, "{\"error\":\"Endpoint not found\"}");
                    }
                    break;
                case "PUT":
                    if (path.matches("/customers/\\d+/?")) {
                        // Memproses permintaan PUT untuk memperbarui data pelanggan berdasarkan ID
                        updateCustomer(exchange);
                    } else if (path.matches("/customers/\\d+/shipping_addresses/\\d+/?")) {
                        // Memproses permintaan PUT untuk memperbarui alamat pengiriman pelanggan
                        updateShippingAddress(exchange);
                    } else {
                        // Jika endpoint tidak ditemukan, kirim respons HTTP 404 (Not Found)
                        sendResponse(exchange, HttpURLConnection.HTTP_NOT_FOUND, "{\"error\":\"Endpoint not found\"}");
                    }
                    break;
                case "DELETE":
                    if (path.matches("/customers/\\d+/?")) {
                        // Memproses permintaan DELETE untuk menghapus pelanggan berdasarkan ID
                        deleteCustomer(exchange);
                    } else {
                        // Jika endpoint DELETE tidak ditemukan, kirim respons HTTP 404 (Not Found)
                        sendResponse(exchange, HttpURLConnection.HTTP_NOT_FOUND, "{\"error\":\"Endpoint not found\"}");
                    }
                    break;
                default:
                    // Jika metode HTTP tidak diizinkan, kirim respons HTTP 405 (Method Not Allowed)
                    sendResponse(exchange, HttpURLConnection.HTTP_BAD_METHOD, "{\"error\":\"Method Not Allowed\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Jika terjadi kesalahan server, kirim respons HTTP 500 (Internal Server Error)
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Internal Server Error\"}");
        }
    }

    // Metode untuk mendapatkan semua pelanggan
    private void getAllCustomers(HttpExchange exchange) throws IOException {
        List<Customer> customers = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM customers";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                // Iterasi melalui hasil query dan menambahkan pelanggan ke dalam daftar
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
            // Mengirimkan respons dengan status 200 (OK) dan daftar pelanggan dalam format JSON
            sendResponse(exchange, HttpURLConnection.HTTP_OK, new JSONArray(customers).toString());
        } catch (Exception e) {
            e.printStackTrace();
            // Jika terjadi kesalahan, kirim respons HTTP 500 (Internal Server Error)
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Could not retrieve customers\"}");
        }
    }

    // Metode untuk mendapatkan pelanggan berdasarkan ID
    private void getCustomerById(HttpExchange exchange) throws IOException {
        // Mendapatkan ID pelanggan dari URL
        String[] parts = exchange.getRequestURI().getPath().split("/");
        int customerId = Integer.parseInt(parts[2]);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM customers WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, customerId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        // Jika pelanggan ditemukan, buat objek Customer dan isi dengan data dari hasil query
                        Customer customer = new Customer();
                        customer.setId(rs.getInt("id"));
                        customer.setEmail(rs.getString("email"));
                        customer.setFirstName(rs.getString("first_name"));
                        customer.setLastName(rs.getString("last_name"));
                        customer.setPhoneNumber(rs.getString("phone_number"));
                        // Mengirimkan respons dengan status 200 (OK) dan data pelanggan dalam format JSON
                        sendResponse(exchange, HttpURLConnection.HTTP_OK, customer.toString());
                    } else {
                        // Jika pelanggan tidak ditemukan, kirim respons HTTP 404 (Not Found)
                        sendResponse(exchange, HttpURLConnection.HTTP_NOT_FOUND, "{\"error\":\"Customer not found\"}");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Jika terjadi kesalahan, kirim respons HTTP 500 (Internal Server Error)
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Could not retrieve customer\"}");
        }
    }

    // Metode untuk mendapatkan daftar kartu kredit/debit pelanggan berdasarkan ID
    private void getCustomerCards(HttpExchange exchange) throws IOException {
        // Mendapatkan ID pelanggan dari URL
        String[] parts = exchange.getRequestURI().getPath().split("/");
        int customerId = Integer.parseInt(parts[2]);

        List<String> cards = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM cards WHERE customer_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, customerId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        // Menambahkan nomor kartu kredit/debit ke dalam daftar
                        cards.add(rs.getString("card_number"));
                    }
                }
            }
            // Mengirimkan respons dengan status 200 (OK) dan daftar kartu dalam format JSON
            sendResponse(exchange, HttpURLConnection.HTTP_OK, new JSONArray(cards).toString());
        } catch (Exception e) {
            e.printStackTrace();
            // Jika terjadi kesalahan, kirim respons HTTP 500 (Internal Server Error)
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Could not retrieve customer cards\"}");
        }
    }

    // Metode untuk mendapatkan daftar subscriptions pelanggan berdasarkan ID
    private void getCustomerSubscriptions(HttpExchange exchange) throws IOException {
        // Mendapatkan ID pelanggan dari URL
        String[] parts = exchange.getRequestURI().getPath().split("/");
        int customerId = Integer.parseInt(parts[2]);

        // Mendapatkan parameter status subscriptions (aktif, cancelled, non-renewing)
        String subscriptionsStatus = exchange.getRequestURI().getQuery();

        List<String> subscriptions = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql;
            if (subscriptionsStatus != null && !subscriptionsStatus.isEmpty()) {
                sql = "SELECT * FROM subscriptions WHERE customer_id = ? AND status = ?";
            } else {
                sql = "SELECT * FROM subscriptions WHERE customer_id = ?";
            }
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, customerId);
                if (subscriptionsStatus != null && !subscriptionsStatus.isEmpty()) {
                    stmt.setString(2, subscriptionsStatus);
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        // Menambahkan informasi subscriptions ke dalam daftar
                        subscriptions.add(rs.getString("subscription_info"));
                    }
                }
            }
            // Mengirimkan respons dengan status 200 (OK) dan daftar subscriptions dalam format JSON
            sendResponse(exchange, HttpURLConnection.HTTP_OK, new JSONArray(subscriptions).toString());
        } catch (Exception e) {
            e.printStackTrace();
            // Jika terjadi kesalahan, kirim respons HTTP 500 (Internal Server Error)
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Could not retrieve customer subscriptions\"}");
        }
    }

    // Metode untuk menambahkan banyak pelanggan baru dari JSON array
    private void addCustomers(HttpExchange exchange) throws IOException {
        // Membaca body permintaan untuk mendapatkan data pelanggan baru
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        JSONArray jsonArray = new JSONArray(requestBody.toString());

        try (Connection conn = DatabaseConnection.getConnection()) {
            // SQL untuk menambahkan pelanggan baru ke dalam database
            String sql = "INSERT INTO customers (id, email, first_name, last_name, phone_number) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                // Melakukan pengulangan untuk setiap objek pelanggan dalam array JSON
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);

                    // Memeriksa apakah ID pelanggan disertakan dalam JSON
                    if (!json.has("id")) {
                        sendResponse(exchange, HttpURLConnection.HTTP_BAD_REQUEST, "{\"error\":\"Missing ID for customer " + i + "\"}");
                        return;
                    }

                    stmt.setInt(1, json.getInt("id"));
                    stmt.setString(2, json.getString("email"));
                    stmt.setString(3, json.getString("first_name"));
                    stmt.setString(4, json.getString("last_name"));
                    stmt.setString(5, json.getString("phone_number"));

                    stmt.addBatch(); // Menambahkan pernyataan ke batch untuk eksekusi bersamaan
                }

                // Menjalankan batch untuk menambahkan semua pelanggan ke database
                int[] rowsInserted = stmt.executeBatch();

                // Memeriksa hasil setiap operasi penambahan
                for (int rows : rowsInserted) {
                    if (rows <= 0) {
                        // Jika salah satu pelanggan tidak berhasil ditambahkan
                        sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Failed to add customers\"}");
                        return;
                    }
                }

                // Jika berhasil menambahkan semua pelanggan
                sendResponse(exchange, HttpURLConnection.HTTP_OK, "{\"message\":\"Customers added successfully\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Jika terjadi kesalahan, kirim respons HTTP 500 (Internal Server Error)
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Could not add customers\"}");
        }
    }


    // Metode untuk memperbarui data pelanggan berdasarkan ID
    private void updateCustomer(HttpExchange exchange) throws IOException {
        // Mendapatkan ID pelanggan dari URL
        String[] parts = exchange.getRequestURI().getPath().split("/");
        int customerId = Integer.parseInt(parts[2]);

        // Membaca body permintaan untuk mendapatkan data pelanggan yang baru
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        JSONObject json = new JSONObject(requestBody.toString());

        try (Connection conn = DatabaseConnection.getConnection()) {
            // SQL untuk melakukan update data pelanggan
            String sql = "UPDATE customers SET email = ?, first_name = ?, last_name = ?, phone_number = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, json.getString("email"));
                stmt.setString(2, json.getString("first_name"));
                stmt.setString(3, json.getString("last_name"));
                stmt.setString(4, json.getString("phone_number"));
                stmt.setInt(5, customerId);
                int rowsUpdated = stmt.executeUpdate();

                if (rowsUpdated > 0) {
                    // Jika data berhasil diperbarui, kirim respons HTTP 200 (OK)
                    sendResponse(exchange, HttpURLConnection.HTTP_OK, "{\"message\":\"Customer updated\"}");
                } else {
                    // Jika pelanggan tidak ditemukan, kirim respons HTTP 404 (Not Found)
                    sendResponse(exchange, HttpURLConnection.HTTP_NOT_FOUND, "{\"error\":\"Customer not found\"}");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Jika terjadi kesalahan, kirim respons HTTP 500 (Internal Server Error)
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Could not update customer\"}");
        }
    }

    // Metode untuk memperbarui alamat pengiriman pelanggan berdasarkan ID
    private void updateShippingAddress(HttpExchange exchange) throws IOException {
        // Implementasi akan ditambahkan sesuai dengan kebutuhan dan skema database
        sendResponse(exchange, HttpURLConnection.HTTP_NOT_IMPLEMENTED, "{\"error\":\"Not Implemented\"}");
    }

    // Metode untuk menonaktifkan produk berdasarkan ID
    private void deactivateItem(HttpExchange exchange) throws IOException {
        // Mendapatkan ID item dari URL
        String[] parts = exchange.getRequestURI().getPath().split("/");
        int itemId = Integer.parseInt(parts[2]);

        try (Connection conn = DatabaseConnection.getConnection()) {
            // SQL untuk menonaktifkan produk berdasarkan ID
            String sql = "UPDATE items SET is_active = false WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, itemId);
                int rowsUpdated = stmt.executeUpdate();

                if (rowsUpdated > 0) {
                    // Jika produk berhasil dinonaktifkan, kirim respons HTTP 200 (OK)
                    sendResponse(exchange, HttpURLConnection.HTTP_OK, "{\"message\":\"Item deactivated\"}");
                } else {
                    // Jika produk tidak ditemukan, kirim respons HTTP 404 (Not Found)
                    sendResponse(exchange, HttpURLConnection.HTTP_NOT_FOUND, "{\"error\":\"Item not found\"}");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Jika terjadi kesalahan, kirim respons HTTP 500 (Internal Server Error)
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Could not deactivate item\"}");
        }
    }

    // Metode untuk menghapus kartu kredit pelanggan berdasarkan ID jika is_primary bernilai false
    private void deleteCustomerCard(HttpExchange exchange) throws IOException {
        // Mendapatkan ID pelanggan dan ID kartu dari URL
        String[] parts = exchange.getRequestURI().getPath().split("/");
        int customerId = Integer.parseInt(parts[2]);
        int cardId = Integer.parseInt(parts[4]);

        try (Connection conn = DatabaseConnection.getConnection()) {
            // SQL untuk menghapus kartu kredit pelanggan jika is_primary bernilai false
            String sql = "DELETE FROM cards WHERE id = ? AND customer_id = ? AND is_primary = false";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, cardId);
                stmt.setInt(2, customerId);
                int rowsDeleted = stmt.executeUpdate();

                if (rowsDeleted > 0) {
                    // Jika kartu kredit berhasil dihapus, kirim respons HTTP 200 (OK)
                    sendResponse(exchange, HttpURLConnection.HTTP_OK, "{\"message\":\"Customer card deleted\"}");
                } else {
                    // Jika kartu kredit tidak ditemukan atau tidak bisa dihapus, kirim respons HTTP 404 (Not Found)
                    sendResponse(exchange, HttpURLConnection.HTTP_NOT_FOUND, "{\"error\":\"Customer card not found or cannot be deleted\"}");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Jika terjadi kesalahan, kirim respons HTTP 500 (Internal Server Error)
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Could not delete customer card\"}");
        }
    }

    // Metode untuk menghapus data pelanggan berdasarkan ID
    private void deleteCustomer(HttpExchange exchange) throws IOException {
        // Mendapatkan ID pelanggan dari URL
        String[] parts = exchange.getRequestURI().getPath().split("/");
        int customerId = Integer.parseInt(parts[2]);

        try (Connection conn = DatabaseConnection.getConnection()) {
            // SQL untuk menghapus pelanggan berdasarkan ID
            String sql = "DELETE FROM customers WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, customerId);
                int rowsDeleted = stmt.executeUpdate();

                if (rowsDeleted > 0) {
                    // Jika pelanggan berhasil dihapus, kirim respons HTTP 200 (OK)
                    sendResponse(exchange, HttpURLConnection.HTTP_OK, "{\"message\":\"Customer deleted\"}");
                } else {
                    // Jika pelanggan tidak ditemukan, kirim respons HTTP 404 (Not Found)
                    sendResponse(exchange, HttpURLConnection.HTTP_NOT_FOUND, "{\"error\":\"Customer not found\"}");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Jika terjadi kesalahan, kirim respons HTTP 500 (Internal Server Error)
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Could not delete customer\"}");
        }
    }


    // Metode untuk mengirimkan respons HTTP
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
