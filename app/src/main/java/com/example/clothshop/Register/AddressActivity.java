package com.example.clothshop.Register;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.clothshop.Login.LoginActivity;
import com.example.clothshop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddressActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private EditText edtReceiver, edtPhone, edtStreet, edtWard, edtDistrict, edtCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        edtReceiver = findViewById(R.id.edtReceiver);
        edtPhone = findViewById(R.id.edtPhone);
        edtStreet = findViewById(R.id.edtStreet);
        edtWard = findViewById(R.id.edtWard);
        edtDistrict = findViewById(R.id.edtDistrict);
        edtCity = findViewById(R.id.edtCity);

        findViewById(R.id.btnSkip).setOnClickListener(v -> goLogin());
        findViewById(R.id.btnSaveAddress).setOnClickListener(v -> saveAddress());
    }

    private void saveAddress() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Not logged in!", Toast.LENGTH_LONG).show();
            return;
        }

        String receiver = edtReceiver.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String street = edtStreet.getText().toString().trim();
        String ward = edtWard.getText().toString().trim();
        String district = edtDistrict.getText().toString().trim();
        String city = edtCity.getText().toString().trim();

        if (receiver.isEmpty() || phone.isEmpty() || street.isEmpty() || district.isEmpty() || city.isEmpty()) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, Object> addr = new HashMap<>();
        addr.put("id", UUID.randomUUID().toString());
        addr.put("receiverName", receiver);
        addr.put("phone", phone);
        addr.put("street", street);
        addr.put("ward", ward);
        addr.put("district", district);
        addr.put("city", city);
        addr.put("isDefault", true);

        db.collection("users").document(user.getUid())
                .update("addresses", FieldValue.arrayUnion(addr))
                .addOnSuccessListener(v -> {
                    Toast.makeText(this, "Saved address!", Toast.LENGTH_SHORT).show();
                    goLogin();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Save failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void goLogin() {
        // Flow bạn muốn: Address xong -> Login -> Main
        auth.signOut();
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
}
