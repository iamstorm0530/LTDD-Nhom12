package com.example.clothshop.model;

import java.util.List;

public class Product {
    public String id;
    public String name;
    public double price;
    public String categoryId;
    public String tag;          // kids | male | female | null
    public List<String> sizes;
    public List<String> colors;
    public int quantity;
    public int soldCount;

    public double averageRating;   // ví dụ 4.5
    public int reviewCount;        // 123 đánh giá
    public List<String> images;
    public String description;
    public String status;
    public Product() {}
    public Product(String id, String name, double price, String categoryId, String tag, List<String> sizes, List<String> colors, int quantity, int soldCount, double averageRating, int reviewCount, List<String> images, String description, String status) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.categoryId = categoryId;
        this.tag = tag;
        this.sizes = sizes;
        this.colors = colors;
        this.quantity = quantity;
        this.soldCount = soldCount;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
        this.images = images;
        this.description = description;
        this.status = status;
    }
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

    public List<String> getSizes() {
        return sizes;
    }

    public void setSizes(List<String> sizes) {
        this.sizes = sizes;
    }

    public List<String> getColors() {
        return colors;
    }

    public void setColors(List<String> colors) {
        this.colors = colors;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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
}