package com.example.clothshop.activity.user;

import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.clothshop.R;

public class ViewReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_review);

        // Ánh xạ View
        TextView tvName = findViewById(R.id.tvProductName);
        TextView tvPrice = findViewById(R.id.tvPrice);
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        TextView tvContent = findViewById(R.id.tvReviewContent);
        TextView tvDate = findViewById(R.id.tvReviewDate);

        // Nút Back
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Nhận dữ liệu (Giả lập dữ liệu đã review trước đó)
        String name = getIntent().getStringExtra("product_name");
        double price = getIntent().getDoubleExtra("product_price", 0);

        // Gán dữ liệu
        if (name != null) tvName.setText(name);
        tvPrice.setText("$" + price);

        // Giả lập rating và nội dung (Sau này lấy từ DB)
        ratingBar.setRating(5);
        tvContent.setText("This product is amazing! The quality is top-notch and fits perfectly. Highly recommended.");

        // 2. Nhận dữ liệu ngày từ Intent
        String date = getIntent().getStringExtra("review_date");

// 3. Hiển thị (Nếu không có dữ liệu thì hiện ngày mặc định)
        if (date != null && !date.isEmpty()) {
            tvDate.setText(date);
        } else {
            // Lấy ngày hiện tại giả lập
            tvDate.setText("03/01/2026");
        }
    }
}
