package com.example.clothshop.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.clothshop.R;

public class LeaveReviewActivity extends AppCompatActivity {

    private TextView tvName, tvPrice, tvCharCount;
    private RatingBar ratingBar;
    private EditText etReview;
    private Button btnSubmit, btnCancel;
    private ImageButton btnBack;

    // Cờ đánh dấu xem người dùng đã thay đổi dữ liệu chưa
    private boolean isDataChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_review);

        initViews();
        setupDataDisplay();
        setupChangeListeners(); // Cài đặt lắng nghe thay đổi

        // 1. Xử lý nút Submit
        btnSubmit.setOnClickListener(v -> handleSubmit());

        // 2. Xử lý nút Back (trên thanh Header)
        btnBack.setOnClickListener(v -> handleBackPress());

        // 3. Xử lý nút Cancel (chức năng giống nút Back)
        btnCancel.setOnClickListener(v -> handleBackPress());

        // 4. Xử lý nút Re-Order
        findViewById(R.id.btnReOrder).setOnClickListener(v -> {
            // 1. Giả lập thêm sản phẩm vào giỏ hàng
            Toast.makeText(this, "Product added to Cart!", Toast.LENGTH_SHORT).show();

            // 2. Chuyển sang màn hình Giỏ hàng (CartActivity)
            Intent intent = new Intent(LeaveReviewActivity.this, CartActivity.class);
            // Có thể thêm flag để xóa các activity cũ nếu muốn
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

            // 3. Đóng màn hình Review lại
            finish();
        });
    }

    private void initViews() {
        tvName = findViewById(R.id.tvProductName);
        tvPrice = findViewById(R.id.tvPrice);
        ratingBar = findViewById(R.id.ratingBar);
        etReview = findViewById(R.id.etReview);
        tvCharCount = findViewById(R.id.tvCharCount);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnCancel = findViewById(R.id.btnCancel);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupDataDisplay() {
        // Nhận dữ liệu từ Intent
        String productName = getIntent().getStringExtra("product_name");
        double productPrice = getIntent().getDoubleExtra("product_price", 0);

        if (productName != null) tvName.setText(productName);
        tvPrice.setText("$" + productPrice);
    }

    // --- LOGIC PHÁT HIỆN THAY ĐỔI DỮ LIỆU ---
    private void setupChangeListeners() {
        // 1. Lắng nghe thay đổi Rating (Sao)
        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) { // Chỉ tính khi người dùng chạm vào
                isDataChanged = true;
            }
        });

        // 2. Lắng nghe thay đổi Text (Nội dung review)
        etReview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isDataChanged = true;

                // --- LOGIC BỘ ĐẾM MỚI ---
                int currentLength = s.length();
                tvCharCount.setText(currentLength + "/500");

                // Hiệu ứng UX: Nếu vượt quá 450 ký tự thì đổi màu số thành Đỏ để cảnh báo
                if (currentLength > 450) {
                    tvCharCount.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                } else {
                    tvCharCount.setTextColor(getResources().getColor(android.R.color.darker_gray));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // --- LOGIC SUBMIT ---
    private void handleSubmit() {
        float rating = ratingBar.getRating();
        String comment = etReview.getText().toString().trim();

        // Điều kiện: Bắt buộc phải có sao (rating > 0)
        if (rating == 0) {
            Toast.makeText(this, "Please give a star rating!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Logic lưu review (Giả lập)
        // Code gọi API lưu rating và comment ở đây...

        Toast.makeText(this, "Review Submitted Successfully!", Toast.LENGTH_SHORT).show();
        finish(); // Đóng màn hình
    }

    // --- LOGIC XỬ LÝ BACK / CANCEL ---
    private void handleBackPress() {
        if (isDataChanged) {
            // Nếu đã nhập liệu -> Hiện cảnh báo
            showDiscardDialog();
        } else {
            // Chưa nhập gì -> Thoát luôn
            finish();
        }
    }

    // Popup Cảnh báo (Giống bên Address)
    private void showDiscardDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Discard Review?")
                .setMessage("You have unsaved changes. Are you sure you want to discard them?")
                .setPositiveButton("Discard", (dialog, which) -> finish()) // Thoát và không lưu
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()) // Ở lại trang
                .show();
    }

    // Xử lý cả nút Back cứng của điện thoại
    @Override
    public void onBackPressed() {
        handleBackPress(); // Gọi chung logic với nút Back trên màn hình
    }
}