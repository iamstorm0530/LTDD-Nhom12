package com.example.appshopbanhang;

import androidx.appcompat.app.AppCompatActivity;
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
public class TrangchuAdmin_Activity extends AppCompatActivity {

    GridView grv2;
    GridView grv1;
    ArrayList<SanPham> mangSPgrv1; // Danh sách cho GridView

    ArrayList<NhomSanPham> mangNSPgrv2; // Danh sách cho ListView

    NhomSanPham_trangChuadmin_Adapter adapterGrv2;
    SanPham_TrangChuAdmin_Adapter adapterGrv1;
    Database database;
    private ViewPager2 viewPager;
    private Handler handler;
    private Runnable runnable;
    private int currentPage = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trangchu_admin);

        ImageButton btncanhan=findViewById(R.id.btncanhan);
        btncanhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //kiểm tra trạng thái đăng nhập của ng dùng
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

                if (!isLoggedIn) {
                    // Chưa đăng nhập, chuyển đến trang login
                    Intent intent = new Intent(getApplicationContext(),Login_Activity.class);
                    startActivity(intent);
                } else {
                    // Đã đăng nhập, chuyển đến trang 2
                    Intent intent = new Intent(getApplicationContext(), TrangCaNhan_admin_Activity.class);
                    startActivity(intent);
                }
            }
        });
        ImageButton btndonhang=findViewById(R.id.btndonhang);
        btndonhang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //kiểm tra trạng thái đăng nhập của ng dùng
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

                if (!isLoggedIn) {
                    // Chưa đăng nhập, chuyển đến trang login
                    Intent intent = new Intent(getApplicationContext(),Login_Activity.class);
                    startActivity(intent);
                } else {
                    // Đã đăng nhập, chuyển đến trang 2
                    Intent intent = new Intent(getApplicationContext(), DonHang_admin_Activity.class);
                    startActivity(intent);
                }
            }
        });
        ImageButton btnsanpham    =findViewById(R.id.btnsanpham);
        btnsanpham.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a=new Intent(getApplicationContext(),Sanpham_admin_Activity.class);
                startActivity(a);
            }
        });
        ImageButton btnnhomsp   =findViewById(R.id.btnnhomsp);
        btnnhomsp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a=new Intent(getApplicationContext(),Nhomsanpham_admin_Actvity.class);
                startActivity(a);
            }
        });
        ImageButton btntaikhoan    =findViewById(R.id.btntaikhoan);
        btntaikhoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a=new Intent(getApplicationContext(),Taikhoan_admin_Activity.class);
                startActivity(a);
            }
        });
        grv2=findViewById(R.id.grv2);
        grv1=findViewById(R.id.grv1);
        mangNSPgrv2= new ArrayList<>(); // Khởi tạo danh sách
        mangSPgrv1= new ArrayList<>(); // Khởi tạo danh sách
        adapterGrv2 = new NhomSanPham_trangChuadmin_Adapter(this, mangNSPgrv2, false) ;

         // false để hiển thị 4 thuộc tính
        grv2.setAdapter(adapterGrv2);

        adapterGrv1= new SanPham_TrangChuAdmin_Adapter(this, mangSPgrv1, false) ;

        grv1.setAdapter(adapterGrv1);


        database = new Database(this, "banhang.db", null, 1);

        Loaddulieugridview2();
        Loaddulieugridview1();
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
        TrangchuAdmin_Activity.ViewPagerAdapter adapter = new TrangchuAdmin_Activity.ViewPagerAdapter(adImages);
        viewPager.setAdapter(adapter);
    }

    private class ViewPagerAdapter extends RecyclerView.Adapter<TrangchuAdmin_Activity.ViewPagerAdapter.SlideViewHolder> {
        private int[] images;

        public ViewPagerAdapter(int[] images) {
            this.images = images;
        }

        @Override
        public TrangchuAdmin_Activity.ViewPagerAdapter.SlideViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slide, parent, false);
            return new TrangchuAdmin_Activity.ViewPagerAdapter.SlideViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TrangchuAdmin_Activity.ViewPagerAdapter.SlideViewHolder holder, int position) {
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
            mangNSPgrv2.add(new NhomSanPham(ma,ten, blob));
        }

        adapterGrv2.notifyDataSetChanged(); // Cập nhật adapter
    }

    private void Loaddulieugridview1() {
        Cursor cursor = database.GetData("SELECT * FROM sanpham order by random() limit 10");
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
                mangSPgrv1.add(new SanPham(masp,tensp,dongia,mota,ghichu,soluongkho,maso,blob));
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(this, "Null load dữ liệu", Toast.LENGTH_SHORT).show();
        }

        adapterGrv1.notifyDataSetChanged();
    }
}