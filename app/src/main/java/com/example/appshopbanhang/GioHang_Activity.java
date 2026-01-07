package com.example.appshopbanhang;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class GioHang_Activity extends AppCompatActivity {
    private ListView listView;
    private GioHangAdapter adapter;
    private GioHangManager gioHangManager;
    private Button thanhtoan;
    private Database database;
    private OrderManager orderManager;
    private TextView txtTongTien; // Khai báo TextView cho tổng tiền

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gio_hang);
        ImageButton btntimkiem = findViewById(R.id.btntimkiem);
        ImageButton btntrangchu = findViewById(R.id.btntrangchu);
        ImageButton btncard = findViewById(R.id.btncart);
        ImageButton btndonhang = findViewById(R.id.btndonhang);
        ImageButton btncanhan = findViewById(R.id.btncanhan);
        thanhtoan = findViewById(R.id.btnthanhtoan);
        listView = findViewById(R.id.listtk);
        TextView textTendn = findViewById(R.id.tendn);

        // Lấy tendn từ SharedPreferences
        SharedPreferences sharedPre = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String tendn = sharedPre.getString("tendn", null);

        if (tendn != null) {
            textTendn.setText(tendn);
        } else {
            Intent intent = new Intent(GioHang_Activity.this, Login_Activity.class);
            startActivity(intent);
            finish(); // Kết thúc activity nếu chưa đăng nhập
            return;
        }

        txtTongTien = findViewById(R.id.tongtien); // Khởi tạo TextView cho tổng tiền
        database = new Database(this, "banhang.db", null, 1);
        database.QueryData("CREATE TABLE IF NOT EXISTS Dathang (" +
                "id_dathang INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "tenkh TEXT, " +
                "diachi TEXT, " +
                "sdt TEXT, " +
                "tongthanhtoan REAL, " +
                "ngaydathang DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "trangthai TEXT);");

        gioHangManager = GioHangManager.getInstance();
        orderManager = new OrderManager(this);

        // Lấy danh sách giỏ hàng và cập nhật giao diện
        List<GioHang> gioHangList = gioHangManager.getGioHangList();
        adapter = new GioHangAdapter(this, gioHangList, txtTongTien);
        listView.setAdapter(adapter);

        // Cập nhật tổng tiền ngay từ giỏ hàng
        txtTongTien.setText(String.valueOf(gioHangManager.getTongTien()));

        // Xử lý sự kiện click thanh toán
        thanhtoan.setOnClickListener(v -> showPaymentDialog());
        btncard.setOnClickListener(new View.OnClickListener() {
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
                    Intent intent = new Intent(getApplicationContext(), GioHang_Activity.class);
                    startActivity(intent);
                }
            }
        });
        btntrangchu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Đã đăng nhập, chuyển đến trang đơn hàng
                Intent intent = new Intent(getApplicationContext(), TrangchuNgdung_Activity.class);

                startActivity(intent);
            }
        });
        btndonhang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        });
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
                    Intent intent = new Intent(getApplicationContext(), TrangCaNhan_nguoidung_Activity.class);
                    startActivity(intent);
                }
            }
        });

        btntimkiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a=new Intent(getApplicationContext(),TimKiemSanPham_Activity.class);
                startActivity(a);
            }
        });

    }

    private void showPaymentDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_thong_tin_thanh_toan);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);

        EditText edtTenKh = dialog.findViewById(R.id.tenkh);
        EditText edtDiaChi = dialog.findViewById(R.id.diachi);
        EditText edtSdt = dialog.findViewById(R.id.sdt);
        Button btnLuu = dialog.findViewById(R.id.btnxacnhandathang);
        TextView tvTongTien = dialog.findViewById(R.id.tienthanhtoan);

        String tongTien = txtTongTien.getText().toString();
        tvTongTien.setText(tongTien);

        btnLuu.setOnClickListener(v -> {
            String tenKh = edtTenKh.getText().toString().trim();
            String diaChi = edtDiaChi.getText().toString().trim();
            String sdt = edtSdt.getText().toString().trim();

            if (tenKh.isEmpty() || diaChi.isEmpty() || sdt.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            } else {
                float tongThanhToan;
                try {
                    tongThanhToan = Float.parseFloat(tongTien.replace(",", ""));
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Có lỗi xảy ra với tổng tiền!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Mở dialog quét mã QR và truyền tổng tiền
                showQrPaymentDialog(tongThanhToan, tenKh, diaChi, sdt);
                dialog.dismiss(); // Đóng dialog sau khi mở dialog quét mã QR
            }
        });

        dialog.show();
    }

    private void showQrPaymentDialog(float tongThanhToan, String tenKh, String diaChi, String sdt) {
        Dialog qrDialog = new Dialog(this);
        qrDialog.setContentView(R.layout.activity_maqr_thanhtoan);
        qrDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        qrDialog.getWindow().setGravity(Gravity.CENTER);

        TextView tvTongTienQr = qrDialog.findViewById(R.id.tongtienthanhtoan);
        Button btnThanhToanThanhCong = qrDialog.findViewById(R.id.btnthanhtoanthanhcong);
        Button btnDungGiaoDich = qrDialog.findViewById(R.id.dunggiaodich);

        tvTongTienQr.setText(tongThanhToan + " VND");

        btnThanhToanThanhCong.setOnClickListener(v -> {
            // Sử dụng thông tin đã truyền từ dialog trước
            if (orderManager != null) {
                long orderId = orderManager.addOrder(tenKh, diaChi, sdt, tongThanhToan);
                if (orderId > 0) {
                    // Lưu thông tin chi tiết đơn hàng
                    List<GioHang> gioHangList = gioHangManager.getGioHangList();
                    for (GioHang item : gioHangList) {
                        String masp = item.getSanPham().getMasp();
                        int soluong = item.getSoLuong();
                        float dongia = item.getSanPham().getDongia();
                        byte[] anhByteArray = item.getSanPham().getAnh();

                        // Gọi phương thức addOrderDetails
                        orderManager.addOrderDetails((int) orderId, masp, soluong, dongia, anhByteArray);
                    }

                    Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                    gioHangManager.clearGioHang(); // Xóa giỏ hàng
                    txtTongTien.setText("0"); // Đặt tổng tiền về 0

                    adapter.notifyDataSetChanged(); // Cập nhật lại giao diện
                    Intent a = new Intent(GioHang_Activity.this, TrangchuNgdung_Activity.class);
                    startActivity(a);
                } else {
                    Toast.makeText(this, "Đặt hàng thất bại!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Không thể xử lý đơn hàng, hãy thử lại!", Toast.LENGTH_SHORT).show();
            }
            qrDialog.dismiss(); // Đóng dialog sau khi xử lý
        });

        btnDungGiaoDich.setOnClickListener(v -> {
            // Đóng dialog quét mã QR
            qrDialog.dismiss();
        });

        qrDialog.show();
    }

}
