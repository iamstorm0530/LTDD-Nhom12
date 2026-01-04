package com.example.clothshop.Register;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.clothshop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VerifyEmailActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        findViewById(R.id.btnOpenMail).setOnClickListener(v -> openMailApp());
        findViewById(R.id.btnResend).setOnClickListener(v -> resendVerify());
        findViewById(R.id.btnIVerified).setOnClickListener(v -> checkVerifiedAndContinue());
    }

    private void openMailApp() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_APP_EMAIL);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void resendVerify() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "No current user!", Toast.LENGTH_LONG).show();
            return;
        }

        user.sendEmailVerification().addOnCompleteListener(t -> {
            Toast.makeText(this,
                    t.isSuccessful() ? "Sent again!" : "Failed: " + (t.getException() != null ? t.getException().getMessage() : ""),
                    Toast.LENGTH_LONG).show();
        });
    }

    private void checkVerifiedAndContinue() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "No current user!", Toast.LENGTH_LONG).show();
            return;
        }

        user.reload().addOnCompleteListener(t -> {
            FirebaseUser u2 = auth.getCurrentUser();
            if (u2 == null) return;

            if (!u2.isEmailVerified()) {
                Toast.makeText(this, "Chưa verify email. Mở mail và bấm link nhé.", Toast.LENGTH_LONG).show();
                return;
            }

            // ✅ VERIFIED => LƯU FIRESTORE NGAY TẠI ĐÂY
            String uid = u2.getUid();
            String name = getIntent().getStringExtra("name");
            String gender = getIntent().getStringExtra("gender");

            Map<String, Object> profile = new HashMap<>();
            profile.put("id", uid);
            profile.put("name", name != null ? name : "User");
            profile.put("email", u2.getEmail());
            profile.put("gender", gender != null ? gender : "other");
            profile.put("avatar", "default");
            profile.put("role", "user");
            profile.put("status", "active");
            profile.put("addresses", new ArrayList<>()); // chưa nhập địa chỉ thì để rỗng

            // SetOptions.merge() để chạy lại không bị lỗi/không đè field khác nếu đã có
            db.collection("users").document(uid)
                    .set(profile, SetOptions.merge())
                    .addOnSuccessListener(v -> {
                        // -> qua trang nhập địa chỉ (có skip)
                        Intent i = new Intent(this, AddressActivity.class);
                        startActivity(i);
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Save profile failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
        });
    }
}
