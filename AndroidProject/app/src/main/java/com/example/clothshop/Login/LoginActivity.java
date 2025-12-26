package com.example.clothshop.Login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.clothshop.MainActivity;
import com.example.clothshop.R;
import com.example.clothshop.util.ApiConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    EditText edtEmail, edtPassword;
    Button btnLogin;
    TextView tvSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 🔥 ÉP WAVE NẰM TRÊN LOGIN1 + LOGIN2
        ImageView imgWave = findViewById(R.id.imgWave);
        imgWave.bringToFront();

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);

        btnLogin.setOnClickListener(v ->
                new LoginTask().execute(
                        edtEmail.getText().toString().trim(),
                        edtPassword.getText().toString().trim()
                )
        );

        tvSignUp.setOnClickListener(v ->
                startActivity(new Intent(
                        LoginActivity.this,
                        com.example.clothshop.Register.RegisterActivity.class
                ))
        );
    }

    class LoginTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                URL url = new URL(ApiConfig.USER_API);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream())
                );

                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }

                JSONArray array = new JSONArray(json.toString());
                for (int i = 0; i < array.length(); i++) {
                    JSONObject u = array.getJSONObject(i);
                    if (u.getString("email").equals(params[0]) &&
                            u.getString("password").equals(params[1]) &&
                            u.getString("status").equals("active")) {
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(LoginActivity.this,
                        "Login success", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(LoginActivity.this,
                        "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
