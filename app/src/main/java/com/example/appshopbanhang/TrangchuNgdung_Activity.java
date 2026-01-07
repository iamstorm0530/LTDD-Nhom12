package com.example.appshopbanhang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class TrangchuNgdung_Activity extends AppCompatActivity {
    GridView grv2;
    GridView grv1;
    RecyclerView recyclerView1,recyclerView2;
    ArrayList<SanPham> mangSPgrv1; // Danh sách cho GridView
    ArrayList<NhomSanPham> mangNSPgrv2; // Danh sách cho ListView
    NhomSanPhamAdapter adapterGrv2;
    SanPhamAdapter adapterGrv1;
    Database database;
    private FloatingActionButton btnhotro;
    ArrayList<SanPham> mangRecy1; // Danh sách sản phẩm cho RecyclerView
    ArrayList<SanPham> mangRecy2; // Danh sách sản phẩm cho RecyclerView
    private ViewPager2 viewPager;
    private Handler handler;
    private Runnable runnable;
    private int currentPage = 0;
    SanPham_Moi_Adapter adapterRecy1; // Adapter cho RecyclerView
    SanPham_Moi_Adapter adapterRecy2; // Adapter cho RecyclerView
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trangchu_ngdung);

        ImageButton btntimkiem = findViewById(R.id.btntimkiem);
        ImageButton btntrangchu = findViewById(R.id.btntrangchu);
        ImageButton btncard = findViewById(R.id.btncart);
        ImageButton btndonhang = findViewById(R.id.btndonhang);
        ImageButton btncanhan = findViewById(R.id.btncanhan);
        EditText timkiem = findViewById(R.id.timkiem);
        TextView textTendn = findViewById(R.id.tendn); // TextView hiển thị tên đăng nhập
        grv2 = findViewById(R.id.grv2);
        grv1 = findViewById(R.id.grv1);
        btnhotro=findViewById(R.id.btnhotro);
        // Khởi tạo RecyclerView
        TextView xemall=findViewById(R.id.xemall);
        // Lấy tên đăng nhập từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String tendn = sharedPreferences.getString("tendn", null);

        // Kiểm tra tên đăng nhập
        if (tendn != null) {
            textTendn.setText(tendn);
        } else {
            Intent intent = new Intent(TrangchuNgdung_Activity.this, Login_Activity.class);
            startActivity(intent);
            finish(); // Kết thúc activity nếu chưa đăng nhập
            return;
        }
        grv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Lấy đối tượng nhóm sản phẩm từ adapter
                NhomSanPham nhomSanPham = mangNSPgrv2.get(position);

                if (nhomSanPham != null) {
                    // Chuyển đến DanhMucSanPham_Activity và truyền mã của nhóm sản phẩm
                    Intent intent = new Intent(TrangchuNgdung_Activity.this, DanhMucSanPham_Activity.class);
                    intent.putExtra("nhomSpId", nhomSanPham.getMa()); // Gửi mã nhóm sản phẩm
                    startActivity(intent);
                }
            }
        });
        btnhotro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrangchuNgdung_Activity.this, ChatBox_Actitvity.class);
                intent.putExtra("tendn", tendn);
                startActivity(intent);



            }
        });
        // Gửi tên đăng nhập qua Intent trong sự kiện click
        btntrangchu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrangchuNgdung_Activity.this, DonHang_User_Activity.class);
                // Gửi tên đăng nhập qua Intent
                intent.putExtra("tendn", tendn); // sử dụng biến tendn đã được xác nhận
                startActivity(intent);
            }
        });
        btntimkiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrangchuNgdung_Activity.this, TimKiemSanPham_Activity.class);

                // Gửi tên đăng nhập qua Intent
                intent.putExtra("tendn", tendn); // Sử dụng biến tendn đã được lấy từ SharedPreferences

                startActivity(intent);
            }
        });
        xemall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrangchuNgdung_Activity.this, TatCaSanPham_Activity.class);
                // Gửi tên đăng nhập qua Intent
                intent.putExtra("tendn", tendn); // sử dụng biến tendn đã được xác nhận
                startActivity(intent);
            }
        });
        btndonhang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrangchuNgdung_Activity.this, DonHang_User_Activity.class);
                intent.putExtra("tendn", tendn); // Gửi tên đăng nhập
                startActivity(intent);
            }
        });
        btncard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrangchuNgdung_Activity.this, GioHang_Activity.class);
                intent.putExtra("tendn", tendn); // Gửi tên đăng nhập
                startActivity(intent);
            }
        });
        btncanhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrangchuNgdung_Activity.this, TrangCaNhan_nguoidung_Activity.class);
                intent.putExtra("tendn", tendn); // Gửi tên đăng nhập
                startActivity(intent);
            }
        });
        // Các sự kiện khác
        timkiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrangchuNgdung_Activity.this, TimKiemSanPham_Activity.class);

                // Gửi tên đăng nhập qua Intent
                intent.putExtra("tendn", tendn); // Sử dụng biến tendn đã được lấy từ SharedPreferences

                startActivity(intent);
            }
        });

        // Khởi tạo danh sách và adapter
        mangNSPgrv2 = new ArrayList<>();
        mangSPgrv1 = new ArrayList<>();
        adapterGrv2 = new NhomSanPhamAdapter(this, mangNSPgrv2, false);
        adapterGrv1 = new SanPhamAdapter(this, mangSPgrv1, false);
        grv2.setAdapter(adapterGrv2);
        grv1.setAdapter(adapterGrv1);
        mangRecy1 = new ArrayList<>(); // Danh sách riêng cho RecyclerView
        mangRecy2 = new ArrayList<>(); // Danh sách riêng cho RecyclerView
        adapterRecy1 = new SanPham_Moi_Adapter(mangRecy1, phongTro -> {
            ChiTietSanPham chiTietSanPham = new ChiTietSanPham(
                    phongTro.getMasp(),
                    phongTro.getTensp(),
                    phongTro.getDongia(),
                    phongTro.getMota(),
                    phongTro.getGhichu(),
                    phongTro.getSoluongkho(),
                    phongTro.getMansp(),
                    phongTro.getAnh()
            );
            Intent intent = new Intent(TrangchuNgdung_Activity.this, ChiTietSanPham_Activity.class);
            intent.putExtra("chitietsanpham", chiTietSanPham);
            intent.putExtra("tendn", tendn);
            startActivity(intent);
        });
        adapterRecy2 = new SanPham_Moi_Adapter(mangRecy2, phongTro -> {
            ChiTietSanPham chiTietSanPham = new ChiTietSanPham(
                    phongTro.getMasp(),
                    phongTro.getTensp(),
                    phongTro.getDongia(),
                    phongTro.getMota(),
                    phongTro.getGhichu(),
                    phongTro.getSoluongkho(),
                    phongTro.getMansp(),
                    phongTro.getAnh()
            );
            Intent intent = new Intent(TrangchuNgdung_Activity.this, ChiTietSanPham_Activity.class);
            intent.putExtra("chitietsanpham", chiTietSanPham);
            intent.putExtra("tendn", tendn);
            startActivity(intent);
        });

        recyclerView1 = findViewById(R.id.list1);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView1.setLayoutManager(layoutManager1);
        recyclerView1.setAdapter(adapterRecy1);

        // Khởi tạo RecyclerView
        recyclerView2= findViewById(R.id.list2);
        LinearLayoutManager layoutManager2= new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView2.setLayoutManager(layoutManager2);
        recyclerView2.setAdapter(adapterRecy2);
        database = new Database(this, "banhang.db", null, 1);

        Loaddulieugridview2();
        Loaddulieugridview1();

        loadreycle1(); // Gọi phương thức để tải dữ liệu vào RecyclerView
        loadreycle2();
        viewPager = findViewById(R.id.sl1);
        // Tạo mảng chứa ID của hình ảnh
        int[] adImages = {
                R.drawable.sl6,
                R.drawable.sl2,
                R.drawable.sl1,
                R.drawable.sl3

        };

        // Thêm ảnh vào ViewPager2
        addImagesToViewPager(adImages);

        // Tạo Handler để tự động chuyển slide
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                currentPage++;
                if (currentPage >= adImages.length) {
                    currentPage = 0; // Reset về đầu
                }
                viewPager.setCurrentItem(currentPage, true);
                handler.postDelayed(this, 6000); // Chuyển sau 2 giây
            }
        };
        handler.postDelayed(runnable, 6000); // Bắt đầu chuyển slide
    }

    private void addImagesToViewPager(int[] adImages) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(adImages);
        viewPager.setAdapter(adapter);
    }

    private class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.SlideViewHolder> {
        private int[] images;

        public ViewPagerAdapter(int[] images) {
            this.images = images;
        }

        @Override
        public SlideViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slide, parent, false);
            return new SlideViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SlideViewHolder holder, int position) {
            holder.adImage.setImageResource(images[position]);
        }

        @Override
        public int getItemCount() {
            return images.length;
        }

        class SlideViewHolder extends RecyclerView.ViewHolder {
            ImageView adImage;

            SlideViewHolder(View itemView) {
                super(itemView);
                adImage = itemView.findViewById(R.id.sl1); // Đảm bảo ID đúng
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); // Dừng chuyển slide khi Activity tạm dừng
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 2000); // Bắt đầu lại khi Activity tiếp tục
    }


    private void Loaddulieugridview2() {
        Cursor dataCongViec = database.GetData("SELECT * from nhomsanpham order by random() limit 10 ");
        mangNSPgrv2.clear();

        while (dataCongViec.moveToNext()) {
            String ma = dataCongViec.getString(0);
            String ten = dataCongViec.getString(1);
            byte[] blob = dataCongViec.getBlob(2); // Lấy mảng byte từ cột chứa ảnh
            mangNSPgrv2.add(new NhomSanPham(ma, ten, blob));
        }

        adapterGrv2.notifyDataSetChanged(); // Cập nhật adapter
    }

    private void Loaddulieugridview1() {
        Cursor cursor = database.GetData(  "SELECT * FROM sanpham " +
                        "WHERE tensp LIKE '%khăn%' " +
                    "OR tensp LIKE '%hoodie%' " +
                    "OR tensp LIKE '%mũ%' " +
                "OR tensp LIKE '%vest%' " +
                        "ORDER BY RANDOM() LIMIT 10"
        );
        mangSPgrv1.clear();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String masp = cursor.getString(0);
                String tensp = cursor.getString(1);
                float dongia = cursor.getFloat(2); // Giữ nguyên là float
                String mota = cursor.getString(3);
                String ghichu = cursor.getString(4);
                int soluongkho = cursor.getInt(5); // Giữ nguyên là int
                String maso = cursor.getString(6);
                byte[] blob = cursor.getBlob(7);
                mangSPgrv1.add(new SanPham(masp, tensp, dongia, mota, ghichu, soluongkho, maso, blob));
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(this, "Null load dữ liệu", Toast.LENGTH_SHORT).show();
        }

        adapterGrv1.notifyDataSetChanged();
    }
//    private void loadreycle1() {
//        Cursor cursor = database.GetData("SELECT * FROM sanpham ORDER BY ROWID DESC LIMIT 8"); // Lấy 8 bản ghi cuối cùng
//        mangRecy1.clear();
//
//        if (cursor != null && cursor.moveToFirst()) {
//            do {
//                String masp = cursor.getString(0);
//                String tensp = cursor.getString(1);
//                float dongia = cursor.getFloat(2);
//                String mota = cursor.getString(3);
//                String ghichu = cursor.getString(4);
//                int soluongkho = cursor.getInt(5);
//                String maso = cursor.getString(6);
//                byte[] blob = cursor.getBlob(7);
//                mangRecy1.add(new SanPham(masp, tensp, dongia, mota, ghichu, soluongkho, maso, blob));
//            } while (cursor.moveToNext());
//        } else {
//            Toast.makeText(this, "Null load dữ liệu", Toast.LENGTH_SHORT).show();
//        }
//
//        adapterRecy1.notifyDataSetChanged(); // Cập nhật adapter
//
//
//    }
private void loadreycle1() {
    Cursor cursor = database.GetData(
            "SELECT * FROM sanpham " +
                    "WHERE tensp LIKE '%sơ mi%' " +
                "OR tensp LIKE '%áo phông%' " +
//                    "OR tensp LIKE '%mì%' " +
//                    "OR tensp LIKE '%sandwich%' " +
                    "ORDER BY RANDOM() LIMIT 10"
    ); // Lấy ngẫu nhiên 10 sản phẩm có tên chứa các từ khóa

    mangRecy1.clear();

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

            mangRecy1.add(new SanPham(masp, tensp, dongia, mota, ghichu, soluongkho, maso, blob));
        } while (cursor.moveToNext());
    } else {
        Toast.makeText(this, "Không có dữ liệu phù hợp", Toast.LENGTH_SHORT).show();
    }

    adapterRecy1.notifyDataSetChanged();
}

//    private void loadreycle2() {
//        Cursor cursor = database.GetData("SELECT * FROM sanpham ORDER BY random() LIMIT 8"); // Lấy 8 bản ghi cuối cùng
//        mangRecy2.clear();
//
//        if (cursor != null && cursor.moveToFirst()) {
//            do {
//                String masp = cursor.getString(0);
//                String tensp = cursor.getString(1);
//                float dongia = cursor.getFloat(2);
//                String mota = cursor.getString(3);
//                String ghichu = cursor.getString(4);
//                int soluongkho = cursor.getInt(5);
//                String maso = cursor.getString(6);
//                byte[] blob = cursor.getBlob(7);
//                mangRecy2.add(new SanPham(masp, tensp, dongia, mota, ghichu, soluongkho, maso, blob));
//            } while (cursor.moveToNext());
//        } else {
//            Toast.makeText(this, "Null load dữ liệu", Toast.LENGTH_SHORT).show();
//        }
//
//        adapterRecy2.notifyDataSetChanged(); // Cập nhật adapter
//
//
//    }
private void loadreycle2() {
    Cursor cursor = database.GetData(
            "SELECT * FROM sanpham " +
                    "WHERE tensp LIKE '%váy%' " +
//                    "OR tensp LIKE '%trà sữa%' " +
//                    "OR tensp LIKE '%sinh tố%' " +
                    "ORDER BY RANDOM() LIMIT 8"
    ); // Lấy ngẫu nhiên 8 sản phẩm có tên chứa soda, trà sữa, hoặc sinh tố

    mangRecy2.clear();

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

            mangRecy2.add(new SanPham(masp, tensp, dongia, mota, ghichu, soluongkho, maso, blob));
        } while (cursor.moveToNext());
    } else {
        Toast.makeText(this, "Không có dữ liệu phù hợp", Toast.LENGTH_SHORT).show();
    }

    adapterRecy2.notifyDataSetChanged(); // Cập nhật adapter
}

}
