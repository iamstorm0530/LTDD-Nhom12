package com.example.appshopbanhang;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList; // Import ArrayList
import java.util.List;

public class DanhMucSanPham_Activity extends AppCompatActivity {
    private GridView grv;
    private ArrayList<SanPham> productList; // Change to ArrayList
    private SanPham_DanhMuc_Adapter productAdapter;
    private DatabaseHelper dbHelper;
    String masp, tendn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_muc_san_pham);
        ImageButton btntimkiem = findViewById(R.id.btntimkiem);
        ImageButton btntrangchu = findViewById(R.id.btntrangchu);
        ImageButton btncard = findViewById(R.id.btncart);
        ImageButton btndonhang = findViewById(R.id.btndonhang);
        ImageButton btncanhan = findViewById(R.id.btncanhan);
        // Initialize the GridView and DatabaseHelper
        grv = findViewById(R.id.grv);
        dbHelper = new DatabaseHelper(this);
        TextView textTendn = findViewById(R.id.tendn); // TextView hiển thị tên đăng nhập

        // Lấy tendn từ SharedPreferences
        SharedPreferences sharedPre = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String tendn = sharedPre.getString("tendn", null);

        if (tendn != null) {
            textTendn.setText(tendn);
        } else {
            Intent intent = new Intent(DanhMucSanPham_Activity.this, Login_Activity.class);
            startActivity(intent);
            finish(); // Kết thúc activity nếu chưa đăng nhập
            return;
        }

        // Retrieve nhomSpId from the Intent
        String nhomSpId = getIntent().getStringExtra("nhomSpId");

        // Check if nhomSpId is not null
        if (nhomSpId != null) {
            // Get the list of products by nhomSpId
            List<SanPham> tempProductList = dbHelper.getProductsByNhomSpId(nhomSpId); // Use a temporary variable
            if (tempProductList != null && !tempProductList.isEmpty()) {
                // Convert List to ArrayList
                productList = new ArrayList<>(tempProductList);
                // Initialize and set the adapter with the product list
                productAdapter = new SanPham_DanhMuc_Adapter(this, productList, false);
                grv.setAdapter(productAdapter);
            } else {
                Toast.makeText(this, "Không tìm thấy sản phẩm nào trong nhóm này!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "ID nhóm sản phẩm không hợp lệ!", Toast.LENGTH_SHORT).show();
        }

        setupNavigationButtons();
        grv.setOnItemClickListener((parent, view, position, id) -> {
            // Lấy sản phẩm tại vị trí được click
            SanPham sanPham = productList.get(position);

            // Tạo Intent để chuyển sang ChiTietSanPham_Activity
            Intent intent = new Intent(DanhMucSanPham_Activity.this, ChiTietSanPham_Activity.class);

            // Truyền dữ liệu sản phẩm qua Intent
            intent.putExtra("masp", sanPham.getMasp());
            intent.putExtra("tensp", sanPham.getTensp());
            intent.putExtra("dongia", sanPham.getDongia());
            intent.putExtra("mota", sanPham.getMota());
            intent.putExtra("ghichu", sanPham.getGhichu());
            intent.putExtra("soluongkho", sanPham.getSoluongkho());
            intent.putExtra("maso", sanPham.getMansp());
            intent.putExtra("anh", sanPham.getAnh());

            // Chuyển đến trang ChiTietSanPham_Activity
            startActivity(intent);
        });

    }
    private void setupNavigationButtons() {
        ImageButton btntrangchu = findViewById(R.id.btntrangchu);
        ImageButton btntimkiem = findViewById(R.id.btntimkiem);
        ImageButton btndonhang = findViewById(R.id.btndonhang);
        ImageButton btngiohang = findViewById(R.id.btncart);
        ImageButton btncanhan = findViewById(R.id.btncanhan);

        btntrangchu.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), TrangchuNgdung_Activity.class);
            startActivity(intent);
        });
        btntimkiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a=new Intent(DanhMucSanPham_Activity.this,TimKiemSanPham_Activity.class);
                startActivity(a);
            }
        });
        btngiohang.setOnClickListener(view -> navigateToCart());
        btndonhang.setOnClickListener(view -> navigateToOrder());
        btncanhan.setOnClickListener(view -> navigateToProfile());
    }

    private void navigateToCart() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            Intent intent = new Intent(getApplicationContext(), Login_Activity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), GioHang_Activity.class);
            startActivity(intent);
        }
    }

    private void navigateToOrder() {
        // Kiểm tra trạng thái đăng nhập của người dùng
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // Đã đăng nhập, chuyển đến trang đơn hàng
            Intent intent = new Intent(getApplicationContext(), DonHang_User_Activity.class);

            // Truyền tendn qua Intent
            intent.putExtra("tendn", tendn);  // Thêm dòng này để truyền tendn

            startActivity(intent);
        } else {
            // Chưa đăng nhập, chuyển đến trang login
            Intent intent = new Intent(getApplicationContext(), Login_Activity.class);
            startActivity(intent);
        }

    }

    private void navigateToProfile() {
        // Kiểm tra trạng thái đăng nhập của người dùng
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // Đã đăng nhập, chuyển đến trang đơn hàng
            Intent intent = new Intent(getApplicationContext(), TrangCaNhan_nguoidung_Activity.class);

            // Truyền tendn qua Intent
            intent.putExtra("tendn", tendn);  // Thêm dòng này để truyền tendn

            startActivity(intent);
        } else {
            // Chưa đăng nhập, chuyển đến trang login
            Intent intent = new Intent(getApplicationContext(), Login_Activity.class);
            startActivity(intent);
        }
    }
}
