package com.example.appshopbanhang;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ChiTietSanPham_Activity extends AppCompatActivity {

     String masp, tendn;
  Button btndathang, btnaddcart;
    private ChiTietSanPham chiTietSanPham;
    private GioHangManager gioHangManager;


    Database database;
    ListView lv;

    ArrayList<DanhGiaSanPham> mang;
    DanhGiaSanPham_Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_san_pham);

        // Khởi tạo các thành phần giao diện
        btndathang = findViewById(R.id.btndathang);
        btnaddcart = findViewById(R.id.btnaddcart);

        TextView tensp = findViewById(R.id.tensp);
        ImageView imgsp = findViewById(R.id.imgsp);
        TextView dongia = findViewById(R.id.dongia);
        TextView ghichu = findViewById(R.id.ghichu);
        TextView mota = findViewById(R.id.mota);
        lv = findViewById(R.id.listtk);


        TextView soluongkho = findViewById(R.id.soluongkho);
        gioHangManager = GioHangManager.getInstance(); // Sử dụng singleton
        TextView textTendn = findViewById(R.id.tendn); // TextView hiển thị tên đăng nhập

        // Lấy tendn từ SharedPreferences
        SharedPreferences sharedPre = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String tendn = sharedPre.getString("tendn", null);

        if (tendn != null) {
            textTendn.setText(tendn);
        } else {
            Intent intent = new Intent(ChiTietSanPham_Activity.this, Login_Activity.class);
            startActivity(intent);
            finish(); // Kết thúc activity nếu chưa đăng nhập
            return;
        }

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();


            // Nhận chi tiết sản phẩm nếu có
            chiTietSanPham = intent.getParcelableExtra("chitietsanpham");

            // Nếu không có chi tiết sản phẩm, bạn có thể xử lý mã sản phẩm theo cách của riêng bạn
            if (chiTietSanPham != null) {
                masp = chiTietSanPham.getMasp(); // Lấy mã sản phẩm từ chi tiết
                tensp.setText(chiTietSanPham.getTensp());
                dongia.setText(chiTietSanPham.getDongia() != null ? String.valueOf(chiTietSanPham.getDongia()) : "Không có dữ liệu");
                mota.setText(chiTietSanPham.getMota() != null ? chiTietSanPham.getMota() : "Không có dữ liệu");
                soluongkho.setText(String.valueOf(chiTietSanPham.getSoluongkho()));
                ghichu.setText(String.valueOf(chiTietSanPham.getGhichu()));
                byte[] anhByteArray = chiTietSanPham.getAnh();
                if (anhByteArray != null && anhByteArray.length > 0) {
                    Bitmap imganhbs = BitmapFactory.decodeByteArray(anhByteArray, 0, anhByteArray.length);
                    imgsp.setImageBitmap(imganhbs);
                } else {
                    imgsp.setImageResource(R.drawable.vest); // Ảnh mặc định
                }
            } else {
                tensp.setText("Không có dữ liệu");
            }


        // Kiểm tra trạng thái đăng nhập và thêm sản phẩm vào giỏ hàng
        btnaddcart.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

            if (!isLoggedIn) {
                Intent loginIntent = new Intent(getApplicationContext(), Login_Activity.class);
                startActivity(loginIntent);
            } else {
                gioHangManager.addItem(chiTietSanPham); // Gọi phương thức addItem
                Toast.makeText(ChiTietSanPham_Activity.this, "Thêm vào giỏ hàng thành công", Toast.LENGTH_SHORT).show();
            }
        });
        // Kiểm tra trạng thái đăng nhập và thêm sản phẩm vào giỏ hàng
        btndathang.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

            if (!isLoggedIn) {
                Intent loginIntent = new Intent(getApplicationContext(), Login_Activity.class);
                startActivity(loginIntent);
            } else {
                gioHangManager.addItem(chiTietSanPham); // Gọi phương thức addItem
                Intent intent1=new Intent(ChiTietSanPham_Activity.this,GioHang_Activity.class);
                startActivity(intent1);
            }
        });
        // Các nút điều hướng
        setupNavigationButtons();
        mang = new ArrayList<>();
        adapter = new DanhGiaSanPham_Adapter(ChiTietSanPham_Activity.this, mang, true);
        lv.setAdapter(adapter);

        database = new Database(this, "banhang.db", null, 1);
        // Tạo bảng DanhGia nếu chưa tồn tại
        database.QueryData("CREATE TABLE IF NOT EXISTS DanhGia (" +
                "id_danhgia INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "masp TEXT, " +
                "id_chitiet TEXT, " +
                "noidung TEXT, " +
                "tendn TEXT, " +
                "sao1 TEXT, " +
                "sao2 TEXT, " +
                "sao3 TEXT, " +
                "sao4 TEXT, " +
                "sao5 TEXT);");


        Loaddulieuanhgia();


    }

    private void Loaddulieuanhgia() {
        Cursor dataCongViec = database.GetData("SELECT * FROM DanhGia WHERE masp = ? order by random() limit 5", new String[]{masp});
        mang.clear();

        while (dataCongViec.moveToNext()) {
            // Kiểm tra chỉ số cột trước khi lấy giá trị
            int idDanhGiaIndex = dataCongViec.getColumnIndex("id_danhgia");
            int idChitietIndex = dataCongViec.getColumnIndex("id_chitiet");
            int noidungIndex = dataCongViec.getColumnIndex("noidung");
            int tendnIndex = dataCongViec.getColumnIndex("tendn");
            int sao1Index = dataCongViec.getColumnIndex("sao1");
            int sao2Index = dataCongViec.getColumnIndex("sao2");
            int sao3Index = dataCongViec.getColumnIndex("sao3");
            int sao4Index = dataCongViec.getColumnIndex("sao4");
            int sao5Index = dataCongViec.getColumnIndex("sao5");

            // Tạo đối tượng DanhGiaSanPham và thêm vào danh sách
            DanhGiaSanPham danhGia = new DanhGiaSanPham(
                    idDanhGiaIndex != -1 ? dataCongViec.getString(idDanhGiaIndex) : null,
                    masp,
                    idChitietIndex != -1 ? dataCongViec.getString(idChitietIndex) : null,
                    noidungIndex != -1 ? dataCongViec.getString(noidungIndex) : null,
                    tendnIndex != -1 ? dataCongViec.getString(tendnIndex) : null,
                    sao1Index != -1 ? dataCongViec.getString(sao1Index) : null,
                    sao2Index != -1 ? dataCongViec.getString(sao2Index) : null,
                    sao3Index != -1 ? dataCongViec.getString(sao3Index) : null,
                    sao4Index != -1 ? dataCongViec.getString(sao4Index) : null,
                    sao5Index != -1 ? dataCongViec.getString(sao5Index) : null
            );

            mang.add(danhGia);
        }
        dataCongViec.close(); // Đóng Cursor để tránh rò rỉ bộ nhớ
        adapter.notifyDataSetChanged(); // Cập nhật danh sách
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
        Intent a=new Intent(ChiTietSanPham_Activity.this,TimKiemSanPham_Activity.class);
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