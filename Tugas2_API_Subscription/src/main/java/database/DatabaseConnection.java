package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // URL JDBC untuk menghubungkan ke database SQLite
    // Path adalah "jdbc:sqlite:Tugas2_API_Subscription/src/main/resources/db_subscription.db".
    // Database berada di dalam folder resources yang sudah saya buat
    private static final String JDBC_URL = "jdbc:sqlite:Tugas2_API_Subscription/src/main/resources/db_subscription.db";

    // Metode untuk mendapatkan koneksi ke database SQLite
    public static Connection getConnection() throws SQLException {
        // Menggunakan DriverManager untuk mendapatkan koneksi berdasarkan URL JDBC yang diberikan
        return DriverManager.getConnection(JDBC_URL);
    }

    // Metode untuk inisialisasi database
    public static void init() {
        // Menggunakan try-with-resources untuk memastikan koneksi ditutup secara otomatis setelah digunakan
        try (Connection conn = getConnection()) {
            if (conn != null) {
                // Jika koneksi berhasil, cetak pesan ke console
                System.out.println("Connected to SQLite database");
            }
        } catch (SQLException e) {
            // Jika terjadi SQLException, cetak stack trace dan lempar runtime exception
            e.printStackTrace();
            throw new RuntimeException("Failed to load SQLite JDBC driver");
        }
    }
}
