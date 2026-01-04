package com.example.clothshop.model;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
    public String id;
    public String name;
    public double price;

    public String categoryId;
    public String tag;

    public List<String> sizes;
    public List<String> colors;

    public int quantity;
    public int soldCount;

    public double averageRating;
    public int reviewCount;

    public List<String> images;
    public String description;
    public String status;

    public Product() {} // BẮT BUỘC cho Firestore

    public Product(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }
}
