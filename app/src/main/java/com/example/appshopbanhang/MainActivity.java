package com.example.appshopbanhang;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {
    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Tạo Handler để chuyển Activity sau 5 giây
        new Handler().postDelayed(() -> {
            // Chuyển sang Login_Activity
            Intent intent = new Intent(MainActivity.this, Login_Activity.class);
            startActivity(intent);
            finish(); // Kết thúc MainActivity nếu không muốn quay lại
        }, 5000); // 5000 milliseconds = 5 seconds
    }
}