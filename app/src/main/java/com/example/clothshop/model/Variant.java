package com.example.clothshop.model;

public class Variant {

    private String id;      // document id của variant
    private String color;   // white, navy, black...
    private String size;    // S, M, L
    private int quantity;   // tồn kho của biến thể

    // 🔥 BẮT BUỘC cho Firestore
    public Variant() {}

    public Variant(String id, String color, String size, int quantity) {
        this.id = id;
        this.color = color;
        this.size = size;
        this.quantity = quantity;
    }

    // ===== GETTER / SETTER =====
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
