package com.example.clothshop.model;

import java.io.Serializable;

public class CartItem implements Serializable {
    private String id;
    private String name;
    private double price;
    private String size;
    private int quantity;
    private int imageResId; // Dùng int để test ảnh mẫu trong drawable, sau này đổi thành String url

    public CartItem(String id, String name, double price, String size, int quantity, int imageResId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.size = size;
        this.quantity = quantity;
        this.imageResId = imageResId;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getSize() { return size; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getImageResId() { return imageResId; }
}
