package com.example.clothshop.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;
import java.util.List;

public class Product  implements Serializable {
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
    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    @PropertyName("isOnSale")
    public boolean isOnSale() {
        return isOnSale;
    }
    @PropertyName("isOnSale")
    public void setOnSale(boolean onSale) {
        this.isOnSale = onSale;
    }
    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }
    public void setSalePercent(Integer salePercent) {
        this.salePercent = salePercent;
    }

    public Double getSalePrice() {
        return isOnSale ? salePrice : null;
    }
    @Exclude
    public double getDisplayPrice() {
        if (isOnSale && salePrice != null && salePrice > 0) {
            return salePrice;
        }
        return price;
    }
    @Exclude
    public Integer getSalePercent() {
        if (!isOnSale) return null;

        if (salePercent != null && salePercent > 0) {
            return salePercent;
        }

        if (salePrice != null && price > 0) {
            return (int) Math.round(
                    100 - (salePrice / price) * 100
            );
        }
        return null;
    }

    // ================= OTHER GETTERS =================
    public String getCategoryId() { return categoryId; }
    public String getTag() { return tag; }
    public String getStatus() { return status; }
    public String getDescription() { return description; }
    public double getAverageRating() { return averageRating; }
    public int getReviewCount() { return reviewCount; }
    public List<String> getImages() { return images; }

    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public void setTag(String tag) { this.tag = tag; }
    public void setStatus(String status) { this.status = status; }
    public void setDescription(String description) { this.description = description; }
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