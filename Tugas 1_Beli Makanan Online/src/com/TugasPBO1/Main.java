package com.TugasPBO1;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login login = new Login();
        Admin admin = new Admin();
        List<Restaurant> restaurants = new ArrayList<>();
        Customer customer = new Customer(admin.getRestaurants());

        namaKelompok();
        namaProject();

        while (true) {
            System.out.println("[]======= Menu ======[]");
            System.out.println("[] 1. Login Admin    []");
            System.out.println("[] 2. Login Customer []");
            System.out.println("[] 3. EXIT           []");
            System.out.println("[]===================[]");
            System.out.println(" ");
            System.out.print("> Pilihan anda: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Membersihkan karakter newline

            switch (choice) {
                case 1:
                    System.out.println(" ");
                    System.out.println("----- Login Admin -----");
                    System.out.print("> Masukkan username: ");
                    String adminUsername = scanner.nextLine();
                    System.out.print("> Masukkan password: ");
                    String adminPassword = scanner.nextLine();

                    if (adminUsername.equals(login.getAdmin_username()) && adminPassword.equals(login.getAdmin_password())) {
                        System.out.println(" ");
                        System.out.println("Berhasil!");
                        admin.adminMenu();
                    }
                    else {
                        System.out.println(" ");
                        System.out.println("> Login gagal! Username atau password salah.");
                        System.out.println(" ");
                    }
                    break;
                case 2:
                    System.out.println(" ");
                    System.out.println("----- Login Customer -----");
                    System.out.print("> Masukkan username: ");
                    String customerUsername = scanner.nextLine();
                    System.out.print("> Masukkan password: ");
                    String customerPassword = scanner.nextLine();
                    if (customerUsername.equals(login.getCustomer_username()) && customerPassword.equals(login.getCustomer_password())) {
                        System.out.println(" ");
                        System.out.println("Berhasil!");
                        customer.customerMenu();
                    }
                    else {
                        System.out.println(" ");
                        System.out.println("> Login gagal! Username atau password salah.");
                        System.out.println(" ");
                    }
                    break;
                case 3:
                    System.out.println("Terimakasih Sudah Membuka Program Pemesanan ini!");
                    scanner.close(); // Menutup scanner sebelum keluar
                    return;
                default:
                    System.out.println("Pilihan Tidak ada atau salah!");
            }
        }
    }

    // Metode untuk mencetak informasi kelompok
    public static void namaKelompok() {
        System.out.println("[]================= Nama Kelompok ==================[]");
        System.out.println("[] 1. Ida Bagus Agung Wiswa Pramana    (2305551092) []");
        System.out.println("[] 2. I Gusti Agung Ngurah Lucien Y.P  (2305551152) []");
        System.out.println("[]==================================================[]");
        System.out.println(" ");
    }

    // Metode untuk mencetak informasi proyek
    public static void namaProject() {
        System.out.println(" ");
        System.out.println("----PROJECT TUGAS PBO 1: BELI MAKANAN ONLINE----");
        System.out.println(" ");
    }
}
