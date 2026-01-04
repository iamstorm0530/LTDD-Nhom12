package com.example.clothshop.model;

public class User {

    private String id;
    private String name;
    private String email;
    private String password;

    // 0 = admin, 1 = user
    private int role;

    private String status;
    private String gender;
    private String phoneNumber;
    private String address;
    private String avatar;

    // Constructor dùng khi REGISTER
    public User(String name,
                String email,
                String password,
                int role,
                String status,
                String gender,
                String phoneNumber,
                String address,
                String avatar) {

        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = status;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.avatar = avatar;
    }

    // ===== GETTER =====
    public String getId() { return id; }

    public String getName() { return name; }

    public String getEmail() { return email; }

    public String getPassword() { return password; }

    public int getRole() { return role; }

    public String getStatus() { return status; }

    public String getGender() { return gender; }

    public String getPhoneNumber() { return phoneNumber; }

    public String getAddress() { return address; }

    public String getAvatar() { return avatar; }
}
