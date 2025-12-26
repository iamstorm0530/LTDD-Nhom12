package com.example.clothshop.Register;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.clothshop.R;
import com.example.clothshop.util.ApiConfig;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RegisterActivity extends AppCompatActivity {

    EditText edtName, edtEmail, edtPassword, edtConfirmPassword;
    Button btnRegister;
    TextView tvLogin; // 1. Khai báo biến

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin); // 2. Ánh xạ ID

        // 3. Sự kiện quay về màn hình Login
        tvLogin.setOnClickListener(v -> {
            finish(); // Đóng Activity hiện tại, quay về Activity trước đó (Login)
        });

        btnRegister.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String confirm = edtConfirmPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this,
                        "Please fill all fields",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirm)) {
                Toast.makeText(this,
                        "Passwords do not match",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            new RegisterTask().execute(name, email, password);
        });

    }

    class RegisterTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                URL url = new URL(ApiConfig.USER_API); // endpoint /user
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty(
                        "Content-Type",
                        "application/json; charset=UTF-8"
                );
                conn.setDoOutput(true);

                JSONObject body = new JSONObject();
                body.put("name", params[0]);
                body.put("email", params[1]);
                body.put("password", params[2]);
                body.put("avatar", ApiConfig.DEFAULT_AVATAR);
                body.put("role", "user");
                body.put("status", "active");

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes(StandardCharsets.UTF_8));
                os.close();

                return conn.getResponseCode() == 201;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(RegisterActivity.this,
                        "Register success. Please login!",
                        Toast.LENGTH_LONG).show();
                finish(); // Đăng ký thành công thì cũng quay về Login
            } else {
                Toast.makeText(RegisterActivity.this,
                        "Register failed",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
