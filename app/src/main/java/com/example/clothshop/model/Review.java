package com.example.clothshop.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;

public class Review {
    @DocumentId
    private String id;          // document id của review
    private String userId;
    private String variantId;
    private String color;
    private String size;
    private String comment;
    private double rating;
    private Timestamp createdAt;
    public Review() {}

    public Review(
            String userId,
            String variantId,
            String color,
            String size,
            String comment,
            double rating,
            Timestamp createdAt
    ) {
        this.userId = userId;
        this.variantId = variantId;
        this.color = color;
        this.size = size;
        this.comment = comment;
        this.rating = rating;
        this.createdAt = createdAt;
    }

    // ================= GETTERS =================
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getVariantId() {
        return variantId;
    }

    public String getColor() {
        return color;
    }

    public String getSize() {
        return size;
    }

    public String getComment() {
        return comment;
    }

    public double getRating() {
        return rating;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    // ================= SETTERS =================
    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setVariantId(String variantId) {
        this.variantId = variantId;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
