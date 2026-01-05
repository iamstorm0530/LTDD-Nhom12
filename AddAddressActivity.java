package com.example.clothshop.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.clothshop.R;
import com.example.clothshop.model.Address;

public class AddAddressActivity extends AppCompatActivity {

    private EditText etTitle, etPhone, etStreet, etCity;
    private Button btnSubmit;
    private ImageButton btnBack;
    private boolean isDataChanged = false; // Cờ đánh dấu đã nhập liệu chưa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        initViews();
        setupValidation();

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> handleBackPress());

        // Xử lý nút Submit
        btnSubmit.setOnClickListener(v -> {
            String fullAddress = etStreet.getText().toString().trim() + ", " + etCity.getText().toString().trim();
            Address newAddress = new Address(
                    System.currentTimeMillis() + "", // Fake ID
                    etTitle.getText().toString().trim(),
                    fullAddress,
                    etPhone.getText().toString().trim(),
                    true // Mặc định chọn luôn cái mới thêm
            );

            // Trả kết quả về AddressListActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("new_address", newAddress);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void initViews() {
        etTitle = findViewById(R.id.etAddressTitle);
        etPhone = findViewById(R.id.etPhone);
        etStreet = findViewById(R.id.etStreet);
        etCity = findViewById(R.id.etCity);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupValidation() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
                isDataChanged = true; // Đánh dấu là user đã nhập gì đó
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        etTitle.addTextChangedListener(watcher);
        etPhone.addTextChangedListener(watcher);
        etStreet.addTextChangedListener(watcher);
        etCity.addTextChangedListener(watcher);
    }

    // Kiểm tra xem đã nhập đủ chưa để bật nút Submit
    private void checkInputs() {
        boolean isValid = !etTitle.getText().toString().trim().isEmpty() &&
                !etPhone.getText().toString().trim().isEmpty() &&
                !etStreet.getText().toString().trim().isEmpty() &&
                !etCity.getText().toString().trim().isEmpty();

        btnSubmit.setEnabled(isValid);
        btnSubmit.setAlpha(isValid ? 1.0f : 0.5f);
    }

    // Xử lý khi bấm Back
    private void handleBackPress() {
        // Kiểm tra xem các ô có trống không
        boolean isEmpty = etTitle.getText().toString().trim().isEmpty() &&
                etPhone.getText().toString().trim().isEmpty() &&
                etStreet.getText().toString().trim().isEmpty() &&
                etCity.getText().toString().trim().isEmpty();

        if (isEmpty) {
            // Chưa nhập gì -> Thoát luôn
            finish();
        } else {
            // Đã nhập dữ liệu -> Hiện cảnh báo
            showDiscardDialog();
        }
    }

    // Hiện Popup Cảnh báo
    private void showDiscardDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Discard Changes?")
                .setMessage("Are you sure you want to discard your changes?")
                .setPositiveButton("Discard", (dialog, which) -> finish()) // Thoát
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()) // Ở lại
                .show();
    }
}