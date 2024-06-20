package Handler;

import Model.Customer;
import Model.Subscription;
import Model.SubscriptionItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import database.DatabaseConnection;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.sql.*;
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
                    } else if (path.matches("/customers/\\d+/subscriptions\\?subscriptions_status=(active|cancelled|non-renewing)")) {
                        // Memproses permintaan GET untuk subscriptions pelanggan dengan status tertentu
                        getCustomerSubscriptionsByStatus(exchange);
                    } else {
                        // Jika endpoint tidak ditemukan, kirim respons HTTP 404 (Not Found)
                        sendResponse(exchange, HttpURLConnection.HTTP_NOT_FOUND, "{\"error\":\"Endpoint not found\"}");
                    }
                    break;
                case "POST":
                    if (path.matches("/customers/?")) {
                        addCustomer(exchange);
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
                        deleteCustomer(exchange);
                    } else if (path.matches("/customers/\\d+/cards/\\d+/?")) {
                        deleteCustomerCard(exchange);
                    } else {
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
                        sendResponse(exchange, HttpURLConnection.HTTP_OK, customer.toJSON().toString());
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

        try (Connection conn = DatabaseConnection.getConnection()) {
            List<String> cards = getCustomerCardsFromDatabase(customerId, conn);

            // Mengirimkan respons dengan status 200 (OK) dan daftar kartu kredit/debit dalam format JSON
            sendResponse(exchange, HttpURLConnection.HTTP_OK, new JSONArray(cards).toString());
        } catch (Exception e) {
            e.printStackTrace();
            // Jika terjadi kesalahan, kirim respons HTTP 500 (Internal Server Error)
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Could not retrieve customer cards\"}");
        }
    }

    // // Metode untuk mengambil cards dari database berdasarkan ID pelanggan
    private List<String> getCustomerCardsFromDatabase(int customerId, Connection conn) throws SQLException {
        List<String> cards = new ArrayList<>();
        String sql = "SELECT masked_number FROM cards WHERE customer_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cards.add(rs.getString("masked_number"));
                }
            }
        }
        return cards;
    }

    // Metode untuk mendapatkan daftar subscriptions pelanggan berdasarkan ID
    private void getCustomerSubscriptions(HttpExchange exchange) throws IOException {
        // Mendapatkan ID pelanggan dari URL
        String[] parts = exchange.getRequestURI().getPath().split("/");
        int customerId = Integer.parseInt(parts[2]);

        try (Connection conn = DatabaseConnection.getConnection()) {
            List<Subscription> subscriptions = getAllCustomerSubscriptionsFromDatabase(customerId, conn);

            // Serialize subscriptions to JSON
            ObjectMapper mapper = new ObjectMapper();
            String jsonResponse = mapper.writeValueAsString(subscriptions);

            // Mengirimkan respons dengan status 200 (OK) dan daftar subscriptions dalam format JSON
            sendResponse(exchange, HttpURLConnection.HTTP_OK, jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            // Jika terjadi kesalahan, kirim respons HTTP 500 (Internal Server Error)
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Could not retrieve customer subscriptions\"}");
        }
    }


    // Metode untuk mengambil semua langganan dari database berdasarkan ID pelanggan
    private List<Subscription> getAllCustomerSubscriptionsFromDatabase(int customerId, Connection conn) throws SQLException {
        List<Subscription> subscriptions = new ArrayList<>();
        String sql = "SELECT * FROM subscriptions WHERE customer_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
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
            }
        }
        return subscriptions;
    }


    // Metode untuk mendapatkan daftar subscriptions pelanggan berdasarkan status subscriptions
    private void getCustomerSubscriptionsByStatus(HttpExchange exchange) throws IOException {
        // Mendapatkan ID pelanggan dan status subscriptions dari URL
        String[] parts = exchange.getRequestURI().getPath().split("/");
        int customerId = Integer.parseInt(parts[2]);
        String subscriptionsStatus = exchange.getRequestURI().getQuery().split("=")[1];

        try (Connection conn = DatabaseConnection.getConnection()) {
            List<String> subscriptions = getCustomerSubscriptionsByStatus(customerId, subscriptionsStatus, conn);

            // Mengirimkan respons dengan status 200 (OK) dan daftar subscriptions berdasarkan status dalam format JSON
            sendResponse(exchange, HttpURLConnection.HTTP_OK, new JSONArray(subscriptions).toString());
        } catch (Exception e) {
            e.printStackTrace();
            // Jika terjadi kesalahan, kirim respons HTTP 500 (Internal Server Error)
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Could not retrieve customer subscriptions by status\"}");
        }
    }

    // Metode untuk menambahkan pelanggan baru
    private void addCustomer(HttpExchange exchange) throws IOException {
        // Mendapatkan payload (data JSON) dari body permintaan
        String requestBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                .lines().reduce("", (accumulator, actual) -> accumulator + actual);

        try {
            // Parsing JSON payload ke objek Customer
            JSONObject jsonObject = new JSONObject(requestBody);
            String email = jsonObject.getString("email");
            String firstName = jsonObject.getString("first_name");
            String lastName = jsonObject.getString("last_name");
            String phoneNumber = jsonObject.getString("phone_number");

            // Validasi data pelanggan (contoh sederhana)
            if (email.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty()) {
                // Jika data tidak lengkap, kirim respons HTTP 400 (Bad Request)
                sendResponse(exchange, HttpURLConnection.HTTP_BAD_REQUEST, "{\"error\":\"Missing required fields\"}");
                return;
            }

            // Simpan data pelanggan ke database
            int customerId = saveCustomer(email, firstName, lastName, phoneNumber);

            // Jika berhasil disimpan, kirim respons HTTP 201 (Created) dengan ID pelanggan baru
            JSONObject responseJson = new JSONObject();
            responseJson.put("id", customerId);
            sendResponse(exchange, HttpURLConnection.HTTP_CREATED, responseJson.toString());
        } catch (Exception e) {
            e.printStackTrace();
            // Jika terjadi kesalahan, kirim respons HTTP 500 (Internal Server Error)
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Could not add customer\"}");
        }
    }

    // Metode untuk memperbarui data pelanggan berdasarkan ID
    private void updateCustomer(HttpExchange exchange) throws IOException {
        // Mendapatkan ID pelanggan dari URL
        String[] parts = exchange.getRequestURI().getPath().split("/");
        int customerId = Integer.parseInt(parts[2]);

        // Mendapatkan payload (data JSON) dari body permintaan
        String requestBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                .lines().reduce("", (accumulator, actual) -> accumulator + actual);

        try {
            // Parsing JSON payload ke objek Customer
            JSONObject jsonObject = new JSONObject(requestBody);
            String email = jsonObject.getString("email");
            String firstName = jsonObject.getString("first_name");
            String lastName = jsonObject.getString("last_name");
            String phoneNumber = jsonObject.getString("phone_number");

            // Validasi data pelanggan (contoh sederhana)
            if (email.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty()) {
                // Jika data tidak lengkap, kirim respons HTTP 400 (Bad Request)
                sendResponse(exchange, HttpURLConnection.HTTP_BAD_REQUEST, "{\"error\":\"Missing required fields\"}");
                return;
            }

            // Perbarui data pelanggan di database
            updateCustomer(customerId, email, firstName, lastName, phoneNumber);

            // Mengirimkan respons dengan status 204 (No Content) untuk mengindikasikan berhasil diperbarui
            sendResponse(exchange, HttpURLConnection.HTTP_NO_CONTENT, "");
        } catch (Exception e) {
            e.printStackTrace();
            // Jika terjadi kesalahan, kirim respons HTTP 500 (Internal Server Error)
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Could not update customer\"}");
        }
    }

    // Metode untuk memperbarui alamat pengiriman pelanggan berdasarkan ID
    private void updateShippingAddress(HttpExchange exchange) throws IOException {
        // Mendapatkan ID pelanggan dan ID alamat dari URL
        String[] parts = exchange.getRequestURI().getPath().split("/");
        int customerId = Integer.parseInt(parts[2]);
        int addressId = Integer.parseInt(parts[4]);

        // Mendapatkan payload (data JSON) dari body permintaan
        String requestBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                .lines().reduce("", (accumulator, actual) -> accumulator + actual);

        try {
            // Parsing JSON payload ke objek alamat pengiriman
            JSONObject jsonObject = new JSONObject(requestBody);
            String address = jsonObject.getString("address");

            // Validasi data alamat
            if (address.isEmpty()) {
                // Jika data tidak lengkap, kirim respons HTTP 400 (Bad Request)
                sendResponse(exchange, HttpURLConnection.HTTP_BAD_REQUEST, "{\"error\":\"Missing required fields\"}");
                return;
            }

            // Perbarui data alamat pengiriman di database
            updateShippingAddress(addressId, address);

            // Mengirimkan respons dengan status 204 (No Content) untuk mengindikasikan berhasil diperbarui
            sendResponse(exchange, HttpURLConnection.HTTP_NO_CONTENT, "");
        } catch (Exception e) {
            e.printStackTrace();
            // Jika terjadi kesalahan, kirim respons HTTP 500 (Internal Server Error)
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Could not update shipping address\"}");
        }
    }

    // Metode untuk menghapus pelanggan berdasarkan ID
    private void deleteCustomer(HttpExchange exchange) throws IOException {
        // Mendapatkan ID pelanggan dari URL
        String[] parts = exchange.getRequestURI().getPath().split("/");
        int customerId = Integer.parseInt(parts[2]);

        try {
            // Hapus pelanggan dari database
            deleteCustomer(customerId);

            // Mengirimkan respons dengan status 204 (No Content) untuk mengindikasikan berhasil dihapus
            sendResponse(exchange, HttpURLConnection.HTTP_NO_CONTENT, "");
        } catch (Exception e) {
            e.printStackTrace();
            // Jika terjadi kesalahan, kirim respons HTTP 500 (Internal Server Error)
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Could not delete customer\"}");
        }
    }

    // Metode untuk menghapus kartu kredit/debit pelanggan berdasarkan ID kartu
    private void deleteCustomerCard(HttpExchange exchange) throws IOException {
        // Mendapatkan ID pelanggan dan ID kartu dari URL
        String[] parts = exchange.getRequestURI().getPath().split("/");
        int customerId = Integer.parseInt(parts[2]);
        int cardId = Integer.parseInt(parts[4]);

        try {
            // Hapus kartu kredit/debit dari database
            deleteCustomerCard(cardId);

            // Mengirimkan respons dengan status 204 (No Content) untuk mengindikasikan berhasil dihapus
            sendResponse(exchange, HttpURLConnection.HTTP_NO_CONTENT, "");
        } catch (Exception e) {
            e.printStackTrace();
            // Jika terjadi kesalahan, kirim respons HTTP 500 (Internal Server Error)
            sendResponse(exchange, HttpURLConnection.HTTP_INTERNAL_ERROR, "{\"error\":\"Could not delete customer card\"}");
        }
    }

    // Metode untuk mengirimkan respons HTTP dengan status dan data tertentu
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    // Method to save customer to database and return the generated customer ID
    private int saveCustomer(String email, String firstName, String lastName, String phoneNumber) throws SQLException {
        String sql = "INSERT INTO customers (email, first_name, last_name, phone_number) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, email);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, phoneNumber);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating customer failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return the generated customer ID
                } else {
                    throw new SQLException("Creating customer failed, no ID obtained.");
                }
            }
        }
    }

    // Method to update customer information in the database
    private void updateCustomer(int customerId, String email, String firstName, String lastName, String phoneNumber) throws SQLException {
        String sql = "UPDATE customers SET email = ?, first_name = ?, last_name = ?, phone_number = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, phoneNumber);
            stmt.setInt(5, customerId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating customer failed, no rows affected.");
            }
        }
    }

    private void updateShippingAddress(int addressId, String address) throws SQLException {
        String sql = "UPDATE shipping_addresses SET address = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, address);
            stmt.setInt(2, addressId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating shipping address failed, no rows affected.");
            }
        }
    }

    private void deleteCustomer(int customerId) throws SQLException {
        String sql = "DELETE FROM customers WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting customer failed, no rows affected.");
            }
        }
    }

    private void deleteCustomerCard(int cardId) throws SQLException {
        String sql = "DELETE FROM cards WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cardId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Deleting customer card failed, no rows affected.");
            }
        }
    }

    private List<String> getCustomerSubscriptions(int customerId, Connection conn) throws SQLException {
        List<String> subscriptions = new ArrayList<>();
        String sql = "SELECT subscription_name FROM customer_subscriptions WHERE customer_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    subscriptions.add(rs.getString("subscription_name"));
                }
            }
        }
        return subscriptions;
    }

    private List<String> getCustomerSubscriptionsByStatus(int customerId, String status, Connection conn) throws SQLException {
        List<String> subscriptions = new ArrayList<>();
        String sql = "SELECT * FROM customer_subscriptions WHERE customer_id = ? AND subscription_status = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            stmt.setString(2, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    subscriptions.add(rs.getString("subscription_name"));
                }
            }
        }
        return subscriptions;
    }
}
