package com.example.omnistock.model;

public class Product {
    private int id;
    private String name;
    private double price;
    private String imageBase64;
    private String description;
    private int stockQty;

    public Product(int id, String name, double price, String imageBase64, String description, int stockQty) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageBase64 = imageBase64;
        this.description = description;
        this.stockQty = stockQty;
    }

    public String getName() { return name; }
    public String getPriceFormatted() { return "PHP " + String.format("%.2f", price); }
    public String getImageBase64() { return imageBase64; }
    public String getDescription() { return description; }
    public int getStockQty() { return stockQty; }
    public int getId() { return id; }
}