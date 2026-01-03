package com.example.clothshop.model;

import com.google.firebase.Timestamp;

public class Review {
    private String userId;
    private String variantId;
    private String color;
    private String size;
    private String comment;

    private double rating;
    private Timestamp createdAt;

    public Review() {}

    public Review(String userId, String variantId, String color, String size, String comment, double rating, Timestamp createdAt) {
        this.userId = userId;
        this.variantId = variantId;
        this.color = color;
        this.size = size;
        this.comment = comment;
        this.rating = rating;
        this.createdAt = createdAt;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVariantId() {
        return variantId;
    }

    public void setVariantId(String variantId) {
        this.variantId = variantId;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

}
