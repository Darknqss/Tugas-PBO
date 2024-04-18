package com.TugasPBO1;

import java.util.Scanner;

public class Customer {
    static Scanner scanner = new Scanner(System.in);

    // Deklarasi Objek
    static Login user = new Login();
    static boolean loggingIn = false;

    // Metode untuk login customer
    public static void customerLogin() {
        do {
            System.out.println(" ");
            System.out.println("----- Login Customer -----");
            System.out.print("> Masukkan username: ");
            String username = scanner.nextLine();
            System.out.print("> Masukkan password: ");
            String password = scanner.nextLine();

            if (username.equals(user.getCustomer_username()) && password.equals(user.getCustomer_password())) {
                System.out.println(" ");
                System.out.println("> Berhasil!");
                loggingIn = true;
            }
            else {
                System.out.println(" ");
                System.out.println("> Gagal!");
                System.out.println(" ");

                System.out.println("Apakah Anda ingin mencoba login kembali?");
                System.out.println("[1] Iya");
                System.out.println("[2] Tidak");
                System.out.print("> Pilihan Anda: ");
                String choice = scanner.nextLine();

                if (choice.equals("2")) {
                    loggingIn = false;
                    return;
                }
            }
        } while (!loggingIn);
    }
}
