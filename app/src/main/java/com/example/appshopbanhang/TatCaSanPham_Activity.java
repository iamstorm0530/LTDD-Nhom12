package com.example.appshopbanhang;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color; // Import thư viện Color
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TatCaSanPham_Activity extends AppCompatActivity {
    GridView grv;
    ArrayList<SanPham> mangSPgrv; // Danh sách cho GridView
    SanPhamAdapter adapterGrv;
    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tat_ca_san_pham);

        // Ánh xạ View
        ImageButton btntimkiem = findViewById(R.id.btntimkiem);
        ImageButton btntrangchu = findViewById(R.id.btntrangchu);
        ImageButton btncard = findViewById(R.id.btncart);
        ImageButton btndonhang = findViewById(R.id.btndonhang);
        ImageButton btncanhan = findViewById(R.id.btncanhan);
        EditText timkiem = findViewById(R.id.timkiem);
        TextView textTendn = findViewById(R.id.tendn); // TextView hiển thị tên đăng nhập
        grv = findViewById(R.id.grv);

        // --- Cập nhật màu chữ tendn thành màu đen ---
        textTendn.setTextColor(Color.BLACK);

        // Lấy tên đăng nhập từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String tendn = sharedPreferences.getString("tendn", null);

        // Kiểm tra tên đăng nhập
        if (tendn != null) {
            textTendn.setText(tendn);
        } else {
            Intent intent = new Intent(TatCaSanPham_Activity.this, Login_Activity.class);
            startActivity(intent);
            finish(); // Kết thúc activity nếu chưa đăng nhập
            return;
        }

        // Gửi tên đăng nhập qua Intent trong sự kiện click
        btntrangchu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TatCaSanPham_Activity.this, TrangchuNgdung_Activity.class);
                intent.putExtra("tendn", tendn);
                startActivity(intent);
            }
        });

        btntimkiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TatCaSanPham_Activity.this, TimKiemSanPham_Activity.class);
                intent.putExtra("tendn", tendn);
                startActivity(intent);
            }
        });

        btndonhang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TatCaSanPham_Activity.this, DonHang_User_Activity.class);
                intent.putExtra("tendn", tendn);
                startActivity(intent);
            }
        });

        btncard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TatCaSanPham_Activity.this, GioHang_Activity.class);
                intent.putExtra("tendn", tendn);
                startActivity(intent);
            }
        });

        btncanhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TatCaSanPham_Activity.this, TrangCaNhan_nguoidung_Activity.class);
                intent.putExtra("tendn", tendn);
                startActivity(intent);
            }
        });

        // Sự kiện click vào ô tìm kiếm
        timkiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TatCaSanPham_Activity.this, TimKiemSanPham_Activity.class);
                intent.putExtra("tendn", tendn);
                startActivity(intent);
            }
        });

        // Khởi tạo danh sách và adapter
        mangSPgrv = new ArrayList<>();
        adapterGrv = new SanPhamAdapter(this, mangSPgrv, false);
        grv.setAdapter(adapterGrv);

        // Khởi tạo database và load dữ liệu
        database = new Database(this, "banhang.db", null, 1);
        Loaddulieusanphamgridview1();
    }

    private void Loaddulieusanphamgridview1() {
        Cursor cursor = database.GetData("SELECT * FROM sanpham ");
        mangSPgrv.clear();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String masp = cursor.getString(0);
                String tensp = cursor.getString(1);
                float dongia = cursor.getFloat(2);
                String mota = cursor.getString(3);
                String ghichu = cursor.getString(4);
                int soluongkho = cursor.getInt(5);
                String maso = cursor.getString(6);
                byte[] blob = cursor.getBlob(7);
                mangSPgrv.add(new SanPham(masp, tensp, dongia, mota, ghichu, soluongkho, maso, blob));
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(this, "Null load dữ liệu", Toast.LENGTH_SHORT).show();
        }

        adapterGrv.notifyDataSetChanged();
    }
}