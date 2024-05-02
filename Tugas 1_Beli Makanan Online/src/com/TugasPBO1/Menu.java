package com.TugasPBO1;

import java.util.HashMap;
import java.util.Map;

public class Menu {
    private int idMenu;
    private Map<String, Double> menuMakanan;
    private Map<String, Double> menuMinuman;

    public Menu(int idMenu) {
        this.idMenu = idMenu;
        menuMakanan = new HashMap<>();
        menuMinuman = new HashMap<>();
    }

    public void tambahMenuMakanan(String nama, double harga) {
        menuMakanan.put(nama, harga);
    }

    public void tambahMenuMinuman(String nama, double harga) {
        menuMinuman.put(nama, harga);
    }

    public Map<String, Double> getMenuMakanan() {
        return menuMakanan;
    }

    public Map<String, Double> getMenuMinuman() {
        return menuMinuman;
    }

    public int getIdMenu() {
        return idMenu;
    }
}
