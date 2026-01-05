package com.example.clothshop.activity.user;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ImageViewCompat;

import com.example.clothshop.R;

public class OrderDetailActivity extends AppCompatActivity {

    private ImageView step1, step2, step3, step4;
    private View line1, line2, line3;
    private TextView tvStep2, tvStep3, tvStep4;
    private Button btnConfirmReceived;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        initViews();

        // Nhận ID đơn hàng (để biết load đơn nào)
        orderId = getIntent().getStringExtra("order_id");

        // GIẢ LẬP TRẠNG THÁI:
        // Bạn có thể đổi số này thành 2, 3, 4 để test giao diện
        // 2: In Progress, 3: Shipped, 4: Delivered
        int currentStep = 2; // Tôi để 4 để test nút Confirm Received

        updateTimelineUI(currentStep);

        // Nút Back
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Xử lý nút Confirm Received
        btnConfirmReceived.setOnClickListener(v -> {
            // Logic: Gọi API báo đã nhận hàng -> Server chuyển status sang Completed
            Toast.makeText(this, "Order Confirmed! Moved to Completed History.", Toast.LENGTH_LONG).show();

            // Quay về danh sách đơn hàng (và F5 lại dữ liệu nếu có API thật)
            finish();
        });
    }

    private void initViews() {
        step1 = findViewById(R.id.step1_dot);
        step2 = findViewById(R.id.step2_dot);
        step3 = findViewById(R.id.step3_dot);
        step4 = findViewById(R.id.step4_dot);

        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);
        line3 = findViewById(R.id.line3);

        tvStep2 = findViewById(R.id.tvStep2);
        tvStep3 = findViewById(R.id.tvStep3);
        tvStep4 = findViewById(R.id.tvStep4);

        btnConfirmReceived = findViewById(R.id.btnConfirmReceived);
    }

    // Hàm tô màu Timeline dựa theo bước hiện tại
    private void updateTimelineUI(int step) {
        int activeColor = Color.parseColor("#000000"); // Đen
        int inactiveColor = Color.parseColor("#E0E0E0"); // Xám

        // Mặc định bước 1 luôn Active

        if (step >= 2) {
            setTint(step2, activeColor);
            line1.setBackgroundColor(activeColor);
            tvStep2.setTextColor(activeColor);
        }

        if (step >= 3) {
            setTint(step3, activeColor);
            line2.setBackgroundColor(activeColor);
            tvStep3.setTextColor(activeColor);
        }

        if (step >= 4) {
            setTint(step4, activeColor);
            line3.setBackgroundColor(activeColor);
            tvStep4.setTextColor(activeColor);

            // QUAN TRỌNG: Chỉ hiện nút Confirm khi ở bước 4 (Delivered)
            btnConfirmReceived.setVisibility(View.VISIBLE);
        } else {
            btnConfirmReceived.setVisibility(View.GONE);
        }
    }

    // Hàm tiện ích để đổi màu ImageView (vector icon)
    private void setTint(ImageView img, int color) {
        ImageViewCompat.setImageTintList(img, ColorStateList.valueOf(color));
    }
}