package com.example.clothshop.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.clothshop.MainActivity; // Import trang chủ của bạn (Guest hoặc User Main)
import com.example.clothshop.R;

public class OrderSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        // 1. Ánh xạ TextView
        TextView tvSuccessTitle = findViewById(R.id.tvSuccessTitle);

        // 2. Nhận dữ liệu từ trang trước gửi sang
        String message = getIntent().getStringExtra("success_message");

        // 3. Nếu có dữ liệu thì thay đổi text, không thì giữ mặc định
        if (message != null) {
            tvSuccessTitle.setText(message);
        }

        Button btnViewOrder = findViewById(R.id.btnViewOrder);
        TextView btnBackHome = findViewById(R.id.btnBackHome);

        // 1. Nút View Order -> Sang trang Lịch sử đơn hàng (Tab Active)
        btnViewOrder.setOnClickListener(v -> {
            Intent intent = new Intent(OrderSuccessActivity.this, OrderHistoryActivity.class);
            // Có thể truyền thêm cờ để mặc định mở tab Active
            startActivity(intent);
            finish();
        });

        // 2. Nút Back to Home -> Về trang chủ, xóa hết các trang mua hàng trước đó
        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(OrderSuccessActivity.this, MainActivity.class); // Hoặc GuestMainActivity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    // Chặn nút Back cứng của điện thoại (không cho back về trang payment)
    @Override
    public void onBackPressed() {
        // Tương tự nút Back to Home
        Intent intent = new Intent(OrderSuccessActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
