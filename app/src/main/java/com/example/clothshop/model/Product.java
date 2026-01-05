package com.example.clothshop.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.util.List;

public class Product {
    @DocumentId
    private String id;
    private String name;
    private double price;

    @PropertyName("isOnSale")
    private boolean isOnSale;

    private Double salePrice;
    private Integer salePercent;

    private String categoryId;
    private String tag;
    private String status;
    private String description;

    private double averageRating;
    private int reviewCount;
    private List<String> images;

    @Exclude
    private List<Variant> variants;

    public Product() {}

    // ================= BASIC GETTERS & SETTERS =================
    public String getId() { return id; }
    public void setId(String id) { this.id = id; } // 🔥 Đã thêm

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    // ================= SALE GETTERS & SETTERS (DATA ONLY) =================
    // Các hàm này trả về dữ liệu THÔ để Firebase đọc/ghi chính xác

    @PropertyName("isOnSale")
    public boolean isOnSale() {
        return isOnSale;
    }

    @PropertyName("isOnSale")
    public void setOnSale(boolean onSale) {
        this.isOnSale = onSale;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    public Integer getSalePercent() {
        return salePercent;
    }

    public void setSalePercent(Integer salePercent) {
        this.salePercent = salePercent;
    }

    // ================= DISPLAY LOGIC  =================
    @Exclude
    public double getDisplayPrice() {
        if (isOnSale && salePrice != null && salePrice > 0) {
            return salePrice;
        }
        return price;
    }
    @Exclude
    public Integer getDisplaySalePercent() {
        if (!isOnSale) return null;

        // Ưu tiên lấy số % đã nhập sẵn
        if (salePercent != null && salePercent > 0) {
            return salePercent;
        }

        // Nếu không có, tự tính toán dựa trên giá gốc và giá sale
        if (salePrice != null && price > 0 && salePrice < price) {
            return (int) Math.round(
                    100 - (salePrice / price) * 100
            );
        }
        return null;
    }
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
    @Exclude
    public List<Variant> getVariants() { return variants; }

    @Exclude
    public void setVariants(List<Variant> variants) {
        this.variants = variants;
    }
    public int getTotalQuantity() {
        if (variants == null) return 0;
        int sum = 0;
        for (Variant v : variants) {
            if (v != null) sum += v.getQuantity();
        }
        return sum;
    }

    public void setReviewStats(double avg, int count) {
        this.averageRating = avg;
        this.reviewCount = count;
    }
}