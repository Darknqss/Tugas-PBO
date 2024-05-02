package com.TugasPBO1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Restaurant {
    private int id;
    private String nama;
    private String alamat;
    private List<Menu> menuList;

    public Restaurant(int id, String nama, String alamat) {
        this.id = id;
        this.nama = nama;
        this.alamat = alamat;
        this.menuList = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public List<Menu> getMenuList() {
        return menuList;
    }

    public void tambahMenu(int jenis, int idMenu, String namaMenu, double harga) {
        Menu menu = new Menu(idMenu);
        if (jenis == 1) {
            menu.tambahMenuMakanan(namaMenu, harga);
        } else if (jenis == 2) {
            menu.tambahMenuMinuman(namaMenu, harga);
        }
        menuList.add(menu);
    }

    public void lihatMenu() {
        if (menuList.isEmpty()) {
            System.out.println("Belum ada menu yang tersedia.");
        } else {
            for (Menu menu : menuList) {
                System.out.println("ID Menu: " + menu.getIdMenu());
                System.out.println("Makanan:");
                for (Map.Entry<String, Double> entry : menu.getMenuMakanan().entrySet()) {
                    String namaMenu = entry.getKey();
                    double harga = entry.getValue();
                    System.out.println("Nama Menu: " + namaMenu + ", Harga(Rp): Rp " + String.format("%.3f", harga));
                }

                System.out.println("\nMinuman:");
                for (Map.Entry<String, Double> entry : menu.getMenuMinuman().entrySet()) {
                    String namaMenu = entry.getKey();
                    double harga = entry.getValue();
                    System.out.println("Nama Menu: " + namaMenu + ", Harga(Rp): Rp " + String.format("%.3f", harga));
                }
            }
        }
    }
}
