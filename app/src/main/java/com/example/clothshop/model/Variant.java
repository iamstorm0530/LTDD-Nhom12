package com.example.clothshop.model;

import com.google.firebase.firestore.DocumentId;

public class Variant {
    @DocumentId
    private String id;
    private String color;
    private String size;
    private int quantity;
    public Variant() {}

    public Variant(String id, String color, String size, int quantity) {
        this.id = id;
        this.color = color;
        this.size = size;
        this.quantity = quantity;
    }
    public String getId() {
        return id;
    }

    public String getColor() {
        return color;
    }
    public String getSize() {
        return size;
    }

    public int getQuantity() {
        return quantity;
    }

    // ================= SETTERS =================
    public void setId(String id) {
        this.id = id;
    }
    public void setColor(String color) {
        this.color = color;
    }

    public void setSize(String size) {
        this.size = size;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
