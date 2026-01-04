package com.example.clothshop.Register;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.clothshop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtName, edtEmail, edtPassword, edtConfirmPassword;
    private RadioButton rbMale, rbFemale, rbOther;
    private Button btnRegister;
    private TextView tvLogin;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);

        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        rbOther = findViewById(R.id.rbOther);

        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        tvLogin.setOnClickListener(v -> finish());

        btnRegister.setOnClickListener(v -> doRegister());
    }

    private void doRegister() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String pass = edtPassword.getText().toString().trim();
        String confirm = edtConfirmPassword.getText().toString().trim();

        String gender = rbMale.isChecked() ? "male" : rbFemale.isChecked() ? "female" : "other";

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pass.equals(confirm)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // RegisterActivity.java (logic chính)
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(r -> {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user == null) return;

                    user.sendEmailVerification()
                            .addOnSuccessListener(v -> {
                                Toast.makeText(this, "Đã gửi mail xác thực. Mở mail bấm link nhé.", Toast.LENGTH_LONG).show();

                                Intent i = new Intent(this, VerifyEmailActivity.class);
                                i.putExtra("name", name);
                                i.putExtra("gender", gender);
                                startActivity(i);
                                finish();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Gửi mail thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Register failed: " + e.getMessage(), Toast.LENGTH_LONG).show());

    }
}
