package com.example.clothshop.model;

import java.io.Serializable;

public class Order implements Serializable {
    private String id;
    private String productName;
    private String productSize;
    private int quantity;
    private double totalPrice;
    private String status; // "Active", "Completed", "Cancelled"
    private int imageResId; // Ảnh giả lập

    // Constructor
    public Order(String id, String productName, String productSize, int quantity, double totalPrice, String status, int imageResId) {
        this.id = id;
        this.productName = productName;
        this.productSize = productSize;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.status = status;
        this.imageResId = imageResId;
    }

    // Getters
    public String getId() { return id; }
    public String getProductName() { return productName; }
    public String getProductSize() { return productSize; }
    public int getQuantity() { return quantity; }
    public double getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
    public int getImageResId() { return imageResId; }
}
