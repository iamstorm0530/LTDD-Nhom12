package com.example.clothshop.model;

import java.io.Serializable;

public class Address implements Serializable {
    private String id;
    private String title;   // Home, Office
    private String addressDetails;
    private String phoneNumber; // MỚI THÊM
    private boolean isSelected;

    public Address(String id, String title, String addressDetails, String phoneNumber, boolean isSelected) {
        this.id = id;
        this.title = title;
        this.addressDetails = addressDetails;
        this.phoneNumber = phoneNumber;
        this.isSelected = isSelected;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAddressDetails() { return addressDetails; }
    public String getPhoneNumber() { return phoneNumber; } // Getter mới
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}
