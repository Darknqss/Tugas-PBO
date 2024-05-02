package com.TugasPBO1;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class Customer {
    private Scanner scanner;
    private List<Order> pesanan;
    private List<Restaurant> restaurants;

    public Customer(List<Restaurant> restaurants) {
        scanner = new Scanner(System.in);
        pesanan = new ArrayList<>();
        this.restaurants = restaurants;
    }

    // Method untuk menampilkan menu customer
    public void customerMenu() {
        int customerChoice;
        do {
            System.out.println(" ");
            System.out.println("[]====== Customer Menu =======[]");
            System.out.println("[] 1. Lihat restaurant        []");
            System.out.println("[] 2. Buat pesanan            []");
            System.out.println("[] 3. Lihat pesanan           []");
            System.out.println("[] 4. Kembali ke login        []");
            System.out.println("[]============================[]");
            System.out.println(" ");
            System.out.print("> Pilihan anda: ");
            customerChoice = scanner.nextInt();
            System.out.println(" ");
            scanner.nextLine(); // Membersihkan karakter newline

            switch (customerChoice) {
                case 1:
                    lihatRestaurant();
                    break;
                case 2:
                    buatPesanan();
                    break;
                case 3:
                    lihatPesanan();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Pilihan Tidak ada atau salah!");
            }
        } while (customerChoice != 4);
    }

    // Method untuk membuat pesanan
    public void buatPesanan() {
        int choice;
        do {
            lihatRestaurant();
            System.out.println("-------------------------");
            System.out.print("> Masukkan ID Restaurant: ");
            int idRestaurant = scanner.nextInt();
            System.out.print("> Masukkan ID Menu: ");
            int idMenu = scanner.nextInt();
            System.out.print("> Masukkan Kuantitas: ");
            int kuantitas = scanner.nextInt();
            System.out.print("> Masukkan Jarak: ");
            double jarak = scanner.nextDouble();
            System.out.print("> Masukkan Total Harga: ");
            double totalHarga = scanner.nextDouble();

            Order order = new Order(idRestaurant, idMenu, kuantitas, jarak, totalHarga);
            pesanan.add(order);
            System.out.println("\nPesanan berhasil dibuat!");

            System.out.println("\nApakah Anda ingin membuat pesanan lagi?");
            System.out.println("[1] Ya");
            System.out.println("[2] Tidak");
            System.out.print("> Pilihan Anda: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Membersihkan karakter newline
        } while (choice == 1);
    }

    // Method untuk melihat pesanan
    public void lihatPesanan() {
        if (pesanan.isEmpty()) {
            System.out.println("\nBelum ada pesanan.");
        } else {
            System.out.println("Daftar Pesanan:");
            for (Order order : pesanan) {
                System.out.println("- ID Restaurant: " + order.getIdRestaurant());
                System.out.println("- ID Menu: " + order.getIdMenu());
                System.out.println("- Kuantitas: " + order.getKuantitas());
                System.out.println("- Jarak(KM) : " + order.getJarak());
                System.out.println("- Total Harga(RP): Rp. " + String.format("%.3f", order.getTotalHarga()));
                System.out.println("-------------------------");
            }
        }
    }

    // Method untuk melihat daftar restoran
    public void lihatRestaurant() {
        List<Restaurant> restaurants = getRestaurants();

        if (restaurants.isEmpty()) {
            System.out.println("\nBelum ada restoran yang tersedia.");
            return;
        }

        System.out.println("Daftar Restoran:");
        for (Restaurant restaurant : restaurants) {
            System.out.println("\nID: " + restaurant.getId() + ", Nama: " + restaurant.getNama() + ", Alamat: " + restaurant.getAlamat());
            System.out.println("\nMenu:");
            restaurant.lihatMenu();
        }
    }

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }
}
