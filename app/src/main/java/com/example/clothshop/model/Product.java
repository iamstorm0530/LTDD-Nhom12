package com.example.clothshop.model;

import java.util.List;

public class Product {

    private String id;
    private String name;
    private double price;
    private String categoryId;
    private String tag;          // kids | male | female | unisex
    private int soldCount;

    private double averageRating;
    private int reviewCount;

    private List<String> images;
    private String description;
    private String status;

    // 🔥 SUBCOLLECTION (load riêng)
    private List<Variant> variants;

    // 🔥 BẮT BUỘC cho Firestore
    public Product() {}

    public Product(
            String id,
            String name,
            double price,
            String categoryId,
            String tag,
            int soldCount,
            double averageRating,
            int reviewCount,
            List<String> images,
            String description,
            String status,
            List<Variant> variants
    ) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.categoryId = categoryId;
        this.tag = tag;
        this.soldCount = soldCount;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
        this.images = images;
        this.description = description;
        this.status = status;
        this.variants = variants;
    }

    // ===== GETTER / SETTER =====
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getSoldCount() {
        return soldCount;
    }

    public void setSoldCount(int soldCount) {
        this.soldCount = soldCount;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Variant> getVariants() {
        return variants;
    }

    public void setVariants(List<Variant> variants) {
        this.variants = variants;
    }

    // ===== HELPER =====

    /** 🔥 Tổng tồn kho = tổng quantity của variants */
    public int getTotalQuantity() {
        if (variants == null) return 0;
        int total = 0;
        for (Variant v : variants) {
            total += v.getQuantity();
        }
        return total;
    }
}
