package com.example.clothshop.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName; // 🔥 Cần import cái này

import java.util.List;

public class Product {

    @DocumentId
    private String id;

    private String name;
    private double price;

    // ===== SALE =====
    // 🔥 FIX: Thêm PropertyName để map chính xác với field "isOnSale" trong DB
    @PropertyName("isOnSale")
    private boolean isOnSale;

    private Double salePrice;      // chỉ đọc khi isOnSale = true
    private Integer salePercent;   // chỉ đọc khi isOnSale = true

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

    // ================= BASIC GETTERS =================
    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }

    // ================= SALE CORE (ĐÃ SỬA) =================

    /**
     * 🔥 FIX: Thêm PropertyName cho Getter để đảm bảo Firebase đọc đúng
     */
    @PropertyName("isOnSale")
    public boolean isOnSale() {
        return isOnSale;
    }

    /**
     * 🔥 FIX: Thêm Setter cho isOnSale (+PropertyName)
     * Firebase BẮT BUỘC cần hàm này để ghi dữ liệu true/false vào biến
     */
    @PropertyName("isOnSale")
    public void setOnSale(boolean onSale) {
        this.isOnSale = onSale;
    }

    /**
     * 🔥 FIX: Thêm Setter cho salePrice
     * Firebase BẮT BUỘC cần hàm này để ghi dữ liệu giá sale vào biến
     */
    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    /**
     * 🔥 FIX: Thêm Setter cho salePercent
     */
    public void setSalePercent(Integer salePercent) {
        this.salePercent = salePercent;
    }

    public Double getSalePrice() {
        return isOnSale ? salePrice : null;
    }

    /**
     * Giá dùng cho hiển thị
     * isOnSale = false → giá gốc
     */
    @Exclude // Hàm logic, không map database
    public double getDisplayPrice() {
        if (isOnSale && salePrice != null && salePrice > 0) {
            return salePrice;
        }
        return price;
    }

    /**
     * Percent hiển thị
     * isOnSale = false → NULL
     */
    @Exclude // Hàm logic, không map database
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