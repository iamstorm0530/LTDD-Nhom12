package com.example.appshopbanhang;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ChiTietDonHang_NguoiDung_3_TrangThai_Khac extends AppCompatActivity {

    DatabaseHelper dbdata;
    Database database;
    ListView listViewChiTiet; // Danh sách hiển thị chi tiết đơn hàng
    ChiTietDonHang_Adapter_3TrangThai_NguoiDung chiTietAdapter; // Adapter để hiển thị chi tiết

    String tendn; // Biến để lưu tên đăng nhập

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_don_hang_nguoidung_3_trang_thai_khac);

        ImageButton btntimkiem = findViewById(R.id.btntimkiem);
        ImageButton btntrangchu = findViewById(R.id.btntrangchu);
        ImageButton btncard = findViewById(R.id.btncart);
        ImageButton btndonhang = findViewById(R.id.btndonhang);
        ImageButton btncanhan = findViewById(R.id.btncanhan);
        TextView textTendn = findViewById(R.id.tendn); // TextView hiển thị tên đăng nhập

        // Lấy tendn từ SharedPreferences
        SharedPreferences sharedPre = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        tendn = sharedPre.getString("tendn", null);

        if (tendn != null) {
            textTendn.setText(tendn);
        } else {
            Intent intent = new Intent(ChiTietDonHang_NguoiDung_3_TrangThai_Khac.this, Login_Activity.class);
            startActivity(intent);
            finish(); // Kết thúc activity nếu chưa đăng nhập
            return;
        }

        // Khởi tạo cơ sở dữ liệu
        dbdata = new DatabaseHelper(this);
        database = new Database(this, "banhang.db", null, 1);

        createTableIfNotExists();

        // Khởi tạo ListView để hiển thị chi tiết đơn hàng
        listViewChiTiet = findViewById(R.id.listtk); // Đảm bảo rằng bạn đã định nghĩa ListView trong layout

        // Lấy ID đơn hàng từ Intent
        String donHangIdStr = getIntent().getStringExtra("donHangId");

        if (donHangIdStr != null) {
            try {
                // Chuyển đổi chuỗi donHangId thành kiểu int
                int donHangId = Integer.parseInt(donHangIdStr);

                // Lấy chi tiết đơn hàng từ database
                List<ChiTietDonHang> chiTietList = dbdata.getChiTietByOrderId(donHangId);

                // Kiểm tra danh sách chi tiết
                if (chiTietList != null && !chiTietList.isEmpty()) {
                    // Sử dụng adapter để hiển thị chi tiết đơn hàng
                    chiTietAdapter = new ChiTietDonHang_Adapter_3TrangThai_NguoiDung(this, chiTietList, tendn);
                    listViewChiTiet.setAdapter(chiTietAdapter); // Gán adapter cho ListView
                } else {
                    Toast.makeText(this, "Không tìm thấy chi tiết cho đơn hàng!", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "ID đơn hàng không hợp lệ!", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Nếu không có ID đơn hàng, lấy tất cả chi tiết đơn hàng
            List<ChiTietDonHang> allChiTietList = dbdata.getAllChiTietDonHang();
            if (allChiTietList != null && !allChiTietList.isEmpty()) {
                chiTietAdapter = new ChiTietDonHang_Adapter_3TrangThai_NguoiDung(this, allChiTietList, tendn);
                listViewChiTiet.setAdapter(chiTietAdapter);
            } else {
                Toast.makeText(this, "Không tìm thấy bất kỳ chi tiết đơn hàng nào!", Toast.LENGTH_SHORT).show();
            }
        }

        // Các sự kiện click cho các nút
        btncard.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
            Intent intent = new Intent(getApplicationContext(), isLoggedIn ? GioHang_Activity.class : Login_Activity.class);
            startActivity(intent);
        });

        btntrangchu.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), TrangchuNgdung_Activity.class);
            startActivity(intent);
        });

        btndonhang.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), DonHang_User_Activity.class);
            startActivity(intent);
        });

        btncanhan.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
            Intent intent = new Intent(getApplicationContext(), isLoggedIn ? TrangCaNhan_nguoidung_Activity.class : Login_Activity.class);
            startActivity(intent);
        });

        btntimkiem.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), TimKiemSanPham_Activity.class);
            startActivity(intent);
        });
    }

    private void createTableIfNotExists() {
        // Tạo bảng Chitietdonhang nếu chưa tồn tại
        database.QueryData("CREATE TABLE IF NOT EXISTS Chitietdonhang (" +
                "id_chitiet INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_dathang INTEGER, " +
                "masp INTEGER, " +
                "soluong INTEGER, " +
                "dongia REAL, " +
                "anh TEXT, " +
                "FOREIGN KEY(id_dathang) REFERENCES Dathang(id_dathang));");
    }
}