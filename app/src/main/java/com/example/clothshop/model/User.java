package com.example.clothshop.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;
import java.util.List;

public class User {
    @DocumentId
    private String id;
    private String name;
    private String email;
    private String avatar;
    private String role;
    private String status;
    private String gender;

    @Exclude
    private List<Address> addresses;
}