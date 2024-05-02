package com.TugasPBO1;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Admin {
    private Scanner scanner;
    private List<Restaurant> restaurants;

    public Admin() {
        scanner = new Scanner(System.in);
        restaurants = new ArrayList<>();
    }

    // Metode untuk mendapatkan daftar restoran
    public List<Restaurant> getRestaurants() {
        return restaurants;
    }

    public void adminMenu() {
        while (true) {
            System.out.println(" ");
            System.out.println("[]====== Menu Admin ======[]");
            System.out.println("[] 1. Lihat restaurant    []");
            System.out.println("[] 2. Tambah restaurant   []");
            System.out.println("[] 3. Hapus restaurant    []");
            System.out.println("[] 4. Kembali ke login    []");
            System.out.println("[]========================[]");
            System.out.println(" ");
            System.out.print("> Pilihan anda: ");

            int choice = scanner.nextInt();
            System.out.println(" ");
            scanner.nextLine(); // Membersihkan karakter newline

            switch (choice) {
                case 1:
                    lihatRestaurant();
                    break;
                case 2:
                    tambahRestaurant();
                    break;
                case 3:
                    hapusRestaurant();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Pilihan Tidak ada atau salah!");
            }
        }
    }

    private void lihatRestaurant() {
        if (restaurants.isEmpty()) {
            System.out.println("Belum ada restoran yang tersedia.");
            return;
        }

        System.out.println("Daftar Restoran:");
        for (Restaurant restaurant : restaurants) {
            System.out.println("\nID: " + restaurant.getId() + ", Nama: " + restaurant.getNama() + ", Alamat: " + restaurant.getAlamat());
            System.out.println("\nMenu:");
            restaurant.lihatMenu();
        }
    }

    public void tambahRestaurant() {
        System.out.print("> Masukan Id Restaurant : ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Membersihkan karakter newline

        System.out.print("> Masukkan nama restoran: ");
        String nama = scanner.nextLine();
        System.out.print("> Masukkan alamat restoran: ");
        String alamat = scanner.nextLine();

        Restaurant restaurant = new Restaurant(id, nama, alamat);
        restaurants.add(restaurant);

        int ulang;
        do {
            System.out.println("\n--------- Restoran " + restaurant.getNama() + " ---------");
            System.out.println("\nTambahkan menu: ");
            System.out.println("1. Makanan");
            System.out.println("2. Minuman");
            System.out.print("> Pilih opsi : ");
            int input = scanner.nextInt();
            System.out.println();
            scanner.nextLine(); // Membersihkan karakter newline

            System.out.println("========================================");
            System.out.print("> Masukan ID menu : ");
            int idMenu = scanner.nextInt();
            scanner.nextLine(); // Membersihkan karakter newline
            System.out.print("> Masukan nama menu : ");
            String namaMenu = scanner.nextLine();
            System.out.print("> Masukan harga(Rp) menu : Rp. ");
            double hargaMenu = scanner.nextDouble();
            scanner.nextLine(); // Membersihkan karakter newline

            restaurant.tambahMenu(input, idMenu, namaMenu, hargaMenu);

            System.out.println("\nMenu berhasil ditambahkan.");

            System.out.println("\nApakah ingin menambah menu lagi?");
            System.out.println("1. Iya");
            System.out.println("2. Tidak");
            System.out.print("> Pilihan Anda: ");
            ulang = scanner.nextInt();
            scanner.nextLine(); // Membersihkan karakter newline
        } while (ulang == 1);
    }


    private void hapusRestaurant() {
        if (restaurants.isEmpty()) {
            System.out.println("Belum ada restoran yang tersedia untuk dihapus.");
            return;
        }

        System.out.print("Masukkan ID restoran yang ingin dihapus: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Membersihkan karakter newline

        boolean found = false;
        for (int i = 0; i < restaurants.size(); i++) {
            if (restaurants.get(i).getId() == id) {
                found = true;
                Restaurant deletedRestaurant = restaurants.remove(i);
                System.out.println("Restoran berhasil dihapus: " + deletedRestaurant);
                break;
            }
        }

        if (!found) {
            System.out.println("Restoran dengan ID tersebut tidak ditemukan.");
        }
    }
}
