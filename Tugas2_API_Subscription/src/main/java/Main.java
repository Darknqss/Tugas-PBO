// Import kelas-kelas yang diperlukan untuk menjalankan server HTTP dan menghubungkan ke database
import com.sun.net.httpserver.HttpServer;
import database.DatabaseConnection;
import Handler.CustomerHandler;
import Handler.ItemHandler;
import Handler.SubscriptionHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

    // API Key saya namakan api_subscription untuk autentikasi mengakses API
    private static final String API_KEY = "api_subscription";

    public static void main(String[] args) {
        // Inisialisasi koneksi ke database
        DatabaseConnection.init(); // biasanya menyiapkan koneksi atau buat skema database jika tidak ada

        try {
            // Menentukan port server, saya pakai 9 sesuai arahan dengan akhiran NIM salah satu anggota kelompok yaitu 152.
            int port = 9152;

            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

            // Endpoint "/customers" di-handle oleh CustomerHandler.
            server.createContext("/customers", new CustomerHandler(API_KEY));
            // Endpoint "/items" di-handle oleh ItemHandler.
            server.createContext("/items", new ItemHandler(API_KEY));
            // Endpoint "/subscriptions" di-handle oleh SubscriptionHandler.
            server.createContext("/subscriptions", new SubscriptionHandler(API_KEY));

            // Menetapkan executor default. Executor bertanggung jawab untuk mengelola thread yang menangani request.
            server.setExecutor(null); // Menetapkan executor default yang disediakan oleh HttpServer

            // Memulai server, sehingga mulai mendengarkan request pada port yang telah ditentukan
            server.start();

            // Menampilkan pesan di console yang menginformasikan bahwa server telah berjalan
            System.out.println("Server mulai dengan port " + port);
            System.out.println("Copy 127.0.0.1:" + port + " atau " + "http://localhost:" + port + " ke dalam Postman untuk testing!");
        } catch (IOException e) {
            // Menangkap dan menampilkan exception jika terjadi kesalahan dalam pembuatan atau memulai server
            e.printStackTrace();
        }
    }

}
