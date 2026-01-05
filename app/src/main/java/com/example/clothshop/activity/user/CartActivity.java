package com.example.clothshop.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clothshop.R;
import com.example.clothshop.adapter.cart.CartAdapter;
import com.example.clothshop.adapter.cart.SwipeToDeleteCallback;
import com.example.clothshop.model.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    // Khai báo các view để hiển thị thông tin
    private RecyclerView rvCartItems;
    private TextView tvSubTotal, tvDelivery, tvTotalCost;
    private Button btnCheckout;
    private List<CartItem> cartList; // Đổi tên biến cartItems thành cartList cho đồng bộ code cũ
    private CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // 1. Ánh xạ View (Kết nối Java với XML)
        initViews();

        // 2. Setup RecyclerView
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));

        // 3. Tạo dữ liệu giả (Sau này sẽ lấy từ Database/API)
        cartList = new ArrayList<>();
        // Lưu ý: Đảm bảo bạn có file ảnh trong res/drawable (ví dụ anh1, anh2) hoặc đổi thành R.drawable.ic_launcher_background để test tạm
        cartList.add(new CartItem("1", "Brown Jacket", 83.97, "XL", 1, R.drawable.anh1));
        cartList.add(new CartItem("2", "Brown Suite", 120.0, "M", 2, R.drawable.anh2));
        cartList.add(new CartItem("1", "Brown Jacket", 83.97, "XL", 1, R.drawable.anh1));
        cartList.add(new CartItem("2", "Brown Suite", 120.0, "M", 2, R.drawable.anh2));
        cartList.add(new CartItem("1", "Brown Jacket", 83.97, "XL", 1, R.drawable.anh1));
        cartList.add(new CartItem("2", "Brown Suite", 120.0, "M", 2, R.drawable.anh2));
        cartList.add(new CartItem("1", "Brown Jacket", 83.97, "XL", 1, R.drawable.anh1));
        cartList.add(new CartItem("2", "Brown Suite", 120.0, "M", 2, R.drawable.anh2));

        // 4. Khởi tạo Adapter và lắng nghe sự kiện thay đổi số lượng/xóa
        adapter = new CartAdapter(cartList, new CartAdapter.OnCartChangeListener() {
            @Override
            public void onCartChanged() {
                // Mỗi khi tăng/giảm số lượng hoặc xóa -> Tính lại tiền
                calculateTotalPrice(cartList);
            }
        });
        rvCartItems.setAdapter(adapter);

        // 5. Gắn tính năng Vuốt để xóa (Swipe to Delete)
        ItemTouchHelper.SimpleCallback simpleCallback = new SwipeToDeleteCallback(adapter);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(rvCartItems);

        // 6. Tính tiền lần đầu tiên khi vừa vào màn hình
        calculateTotalPrice(cartList);

        // 7. Xử lý sự kiện nút Checkout
        btnCheckout.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
            // Truyền tổng tiền
            // Truyền danh sách (ép kiểu về ArrayList để chắc chắn Serializable hoạt động)
            intent.putExtra("cart_list", (ArrayList<CartItem>) cartList);
            // Nếu bạn muốn truyền total cost:
            // intent.putExtra("total_cost", currentTotalCost);
            startActivity(intent);
        });
    }

    // Hàm ánh xạ View
    private void initViews() {
        rvCartItems = findViewById(R.id.rvCartItems);

        tvSubTotal = findViewById(R.id.tvSubTotal);
        tvDelivery = findViewById(R.id.tvDelivery);
        tvTotalCost = findViewById(R.id.tvTotalCost);

        btnCheckout = findViewById(R.id.btnCheckout);

        // Xử lý nút Back
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    // Hàm logic tính tổng tiền
    private void calculateTotalPrice(List<CartItem> list) {
        double subTotal = 0;

        // Cộng dồn tiền hàng: Giá * Số lượng
        for (CartItem item : list) {
            subTotal += item.getPrice() * item.getQuantity();
        }

        // Thiết lập phí cố định
        double deliveryFee = 25.00;
        double discount = 0; // Có thể set cứng -35.00 nếu muốn giống design
        // Ví dụ: discount = 35.00;

        // Nếu giỏ hàng trống -> Phí ship và giảm giá về 0
        if (list.isEmpty()) {
            deliveryFee = 0;
            discount = 0;
            btnCheckout.setEnabled(false); // Khóa nút thanh toán
            btnCheckout.setAlpha(0.5f);    // Làm mờ nút
        } else {
            btnCheckout.setEnabled(true);
            btnCheckout.setAlpha(1.0f);
        }

        // Tính tổng cuối cùng
        double totalCost = subTotal + deliveryFee - discount;

        // Hiển thị lên giao diện (Format 2 số thập phân: %.2f)
        tvSubTotal.setText(String.format("$%.2f", subTotal));
        tvDelivery.setText(String.format("$%.2f", deliveryFee));

        tvTotalCost.setText(String.format("$%.2f", totalCost));
    }
}