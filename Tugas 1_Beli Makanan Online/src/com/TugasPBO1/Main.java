package com.TugasPBO1;

import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        namaKelompok();

        while (true) {
            namaProject();

            System.out.println("[]======= Menu ======[]");
            System.out.println("[] 1. Login Admin    []");
            System.out.println("[] 2. Login Customer []");
            System.out.println("[] 3. EXIT           []");
            System.out.println("[]===================[]");
            System.out.println(" ");
            System.out.print("> Pilihan anda: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("XD!");
                    break;
                case 2:
                    Customer.customerLogin(); // Memanggil metode customerLogin dari kelas Customer
                    break;
                case 3:
                    System.out.println("Terimakasih Sudah Membuka Program Pemesanan ini!");
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