package com.example.clothshop.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothshop.R;
import com.example.clothshop.adapter.checkout.CheckoutAdapter;
import com.example.clothshop.model.Address;
import com.example.clothshop.model.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private RecyclerView rvCheckoutItems;
    private TextView btnChangeAddress;
    private Button btnContinuePayment;
    private List<CartItem> checkoutList;
    private static final int REQUEST_CODE_ADDRESS = 101; // Mã định danh

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        initViews();

        // 1. Nhận dữ liệu từ Cart
        if (getIntent().getSerializableExtra("cart_list") != null) {
            checkoutList = (ArrayList<CartItem>) getIntent().getSerializableExtra("cart_list");
        } else {
            checkoutList = new ArrayList<>();
        }

        // 2. Setup RecyclerView
        rvCheckoutItems.setLayoutManager(new LinearLayoutManager(this));
        CheckoutAdapter adapter = new CheckoutAdapter(checkoutList);
        rvCheckoutItems.setAdapter(adapter);

        // 3. Xử lý nút Change Address -> Sang trang 3 (AddressListActivity)
        btnChangeAddress.setOnClickListener(v -> {
            Intent intent = new Intent(CheckoutActivity.this, AddressListActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADDRESS);
        });

        // 4. Xử lý nút Continue -> Sang trang 4 (Payment)
        btnContinuePayment.setOnClickListener(v -> {
             Intent intent = new Intent(CheckoutActivity.this, PaymentMethodActivity.class);
             startActivity(intent);
        });

        // 5. Nút Back (Vì dùng include layout nên tìm ID bên trong)
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ADDRESS && resultCode == RESULT_OK && data != null) {
            // Nhận địa chỉ mới trả về
            Address newAddress = (Address) data.getSerializableExtra("selected_address");

            if (newAddress != null) {
                // Cập nhật lên giao diện Checkout
                TextView tvTitle = findViewById(R.id.tvAddressTitle);
                TextView tvDetail = findViewById(R.id.tvAddressDetail);

                tvTitle.setText(newAddress.getTitle());
                tvDetail.setText(newAddress.getAddressDetails());
            }
        }
    }

    private void initViews() {
        rvCheckoutItems = findViewById(R.id.rvCheckoutItems);
        btnChangeAddress = findViewById(R.id.btnChangeAddress);
        btnContinuePayment = findViewById(R.id.btnContinuePayment);
        // btnBack tìm trực tiếp ở trên
    }
}