package com.example.clothshop.model;

import java.io.Serializable;

public class Review implements Serializable {
    private String id;
    private String userName;
    private String userAvatar; // URL hoặc resource ID
    private float rating; // 1.0 - 5.0
    private String comment;
    private String date; // "12 Oct 2023"

    public Review(String id, String userName, float rating, String comment, String date) {
        this.id = id;
        this.userName = userName;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
    }

    // Getters
    public String getUserName() { return userName; }
    public float getRating() { return rating; }
    public String getComment() { return comment; }
    public String getDate() { return date; }
}