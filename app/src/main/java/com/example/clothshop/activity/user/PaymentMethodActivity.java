package com.example.clothshop.activity.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.clothshop.R;

public class PaymentMethodActivity extends AppCompatActivity {

    private RadioButton rbCOD, rbVNPAY;
    private Button btnConfirm;
    private TextView tvTotalAmount;
    private double totalAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        initViews();

        // Nhận dữ liệu tổng tiền từ Checkout
        // Lưu ý: Ở CheckoutActivity bạn nhớ putExtra("total_amount", value) nhé
        totalAmount = getIntent().getDoubleExtra("total_amount", 0);
        tvTotalAmount.setText(String.format("$%.2f", totalAmount));

        btnConfirm.setOnClickListener(v -> handlePayment());

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void initViews() {
        rbCOD = findViewById(R.id.rbCOD);
        rbVNPAY = findViewById(R.id.rbVNPAY);
        btnConfirm = findViewById(R.id.btnConfirmPayment);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
    }

    private void handlePayment() {
        if (rbCOD.isChecked()) {
            // COD -> Truyền "Order placed successfully"
            processOrderSuccess("Order placed successfully");

        } else if (rbVNPAY.isChecked()) {
            simulateVnPay();
        }
    }


    // Giả lập quá trình thanh toán VNPAY (Loading 2 giây)
    private void simulateVnPay() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing VNPAY Payment...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Giả lập độ trễ mạng 2s
        new Handler().postDelayed(() -> {
            progressDialog.dismiss();
            // VNPAY -> Truyền "Payment Successful!"
            processOrderSuccess("Payment Successful!");
        }, 2000);
    }

    private void processOrderSuccess(String message) {
        Intent intent = new Intent(PaymentMethodActivity.this, OrderSuccessActivity.class);
        // Gửi câu thông báo sang trang kia
        intent.putExtra("success_message", message);
        startActivity(intent);
        finish();
    }
}
