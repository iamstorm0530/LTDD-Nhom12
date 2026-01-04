package com.example.clothshop.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import java.util.List;

public class Product {
    @DocumentId
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
    @Exclude
    private List<Variant> variants;

    // ================= CONSTRUCTOR =================
    public Product() {}

    // ================= GETTERS =================
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
    @Exclude
    public List<Variant> getVariants() {
        return variants;
    }
    @Exclude
    public void setVariants(List<Variant> variants) {
        this.variants = variants;
    }
    public int getTotalQuantity() {
        if (variants == null) return 0;
        int sum = 0;
        for (Variant v : variants) {
            if (v != null) {
                sum += v.getQuantity();
            }
        }
        return sum;
    }
    public void setReviewStats(double avg, int count) {
        this.averageRating = avg;
        this.reviewCount = count;
    }
}
