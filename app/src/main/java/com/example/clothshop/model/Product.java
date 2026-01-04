package com.example.clothshop.model;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {

    private String id;
    private String name;
    private double price;
    private String categoryId;
    private String tag;
    private String status;
    private String description;

    private double averageRating;
    private int reviewCount;

    private List<String> images;
    private List<Variant> variants;

    public Product() {}

    // ===== GETTERS =====
    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getCategoryId() { return categoryId; }
    public String getTag() { return tag; }
    public String getStatus() { return status; }
    public String getDescription() { return description; }
    public double getAverageRating() { return averageRating; }
    public int getReviewCount() { return reviewCount; }
    public List<String> getImages() { return images; }
    public List<Variant> getVariants() { return variants; }

    // ===== SETTERS =====
    public void setId(String id) { this.id = id; }
    public void setVariants(List<Variant> variants) { this.variants = variants; }

    public int getTotalQuantity() {
        if (variants == null) return 0;
        int sum = 0;
        for (Variant v : variants) sum += v.getQuantity();
        return sum;
    }

    public void setReviewStats(double avg, int count) {
        this.averageRating = avg;
        this.reviewCount = count;
    }
}