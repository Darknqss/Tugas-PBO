package com.SubscriptionAPI;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;
import com.SubscriptionAPI.database.DatabaseConnection;
import com.SubscriptionAPI.Handler.CustomerHandler;
import com.SubscriptionAPI.Handler.ItemHandler;
import com.SubscriptionAPI.Handler.SubscriptionHandler;

public class Main {

    public static void main(String[] args) {
        // Inisialisasi koneksi ke database
        DatabaseConnection.connect();

        // Inisialisasi HTTP server
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(9090), 0);
            server.createContext("/customers", new CustomerHandler());
            server.createContext("/subscriptions", new SubscriptionHandler());
            server.createContext("/items", new ItemHandler());
            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
            System.out.println("Server started on port 9090");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}