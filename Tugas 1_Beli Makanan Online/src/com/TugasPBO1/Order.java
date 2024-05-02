package com.TugasPBO1;

public class Order {
    private int idRestaurant;
    private int idMenu;
    private int kuantitas;
    private double jarak;
    private double totalHarga;

    public Order(int idRestaurant, int idMenu, int kuantitas, double jarak, double totalHarga) {
        this.idRestaurant = idRestaurant;
        this.idMenu = idMenu;
        this.kuantitas = kuantitas;
        this.jarak = jarak;
        this.totalHarga = totalHarga;
    }

    // Getter dan Setter
    public int getIdRestaurant() {
        return idRestaurant;
    }

    public void setIdRestaurant(int idRestaurant) {
        this.idRestaurant = idRestaurant;
    }

    public int getIdMenu() {
        return idMenu;
    }

    public void setIdMenu(int idMenu) {
        this.idMenu = idMenu;
    }

    public int getKuantitas() {
        return kuantitas;
    }

    public void setKuantitas(int kuantitas) {
        this.kuantitas = kuantitas;
    }

    public double getJarak() {
        return jarak;
    }

    public void setJarak(double jarak) {
        this.jarak = jarak;
    }

    public double getTotalHarga() {
        return totalHarga;
    }

    public void setTotalHarga(double totalHarga) {
        this.totalHarga = totalHarga;
    }
}
