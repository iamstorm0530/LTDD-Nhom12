package com.example.appshopbanhang;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class Login_Activity extends AppCompatActivity {

    private Database database;
    private String tendn;
    private Handler handler = new Handler();
    private Runnable timeoutRunnable;
    private static final long TIMEOUT_DURATION = 3000000; // 300 giây

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnLogin = findViewById(R.id.btnLogin);
        EditText tdn = findViewById(R.id.tdn);
        EditText mk = findViewById(R.id.mk);
        TextView dangki = findViewById(R.id.dangki);
        TextView qmk = findViewById(R.id.qmk);
        qmk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DoiMatKhau_Activity.class);
                startActivity(intent);
            }
        });
        database = new Database(this, "banhang.db", null, 1);

        // Chuyển đến activity đăng ký tài khoản
        dangki.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), DangKiTaiKhoan_Activity.class);
            startActivity(intent);
        });

        // Xử lý sự kiện đăng nhập
        btnLogin.setOnClickListener(v -> {
            String username = tdn.getText().toString();
            String password = mk.getText().toString();

            if (validateLogin(username, password)) {
                // Gán tên đăng nhập cho biến tendn
                tendn = username;

                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("tendn", tendn); // Lưu tên đăng nhập
                editor.putBoolean("isLoggedIn", true);  // Đánh dấu người dùng đã đăng nhập
                editor.apply(); // Lưu các thay đổi

                // Khởi động Timer
                startAutoLogoutTimer();

                // Chuyển đến activity phù hợp theo quyền
                String quyen = getUserQuyen(username);
                Intent intent;

                if (quyen.equals("admin")) {
                    intent = new Intent(Login_Activity.this, TrangchuAdmin_Activity.class);
                    Toast.makeText(this, "Đăng nhập với quyền Admin", Toast.LENGTH_SHORT).show();
                } else if (quyen.equals("user")) {
                    intent = new Intent(Login_Activity.this, TrangchuNgdung_Activity.class);
                    intent.putExtra("tendn", tendn); // Truyền tên đăng nhập
                    Toast.makeText(this, "Đăng nhập với quyền User", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Quyền không xác định", Toast.LENGTH_SHORT).show();
                    return; // Ngăn việc chuyển đến activity nếu quyền không xác định
                }

                startActivity(intent);
                finish(); // Kết thúc Login Activity
            } else {
                Toast.makeText(Login_Activity.this, "Tên đăng nhập hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm kiểm tra thông tin đăng nhập
    private boolean validateLogin(String username, String password) {
        Cursor cursor = database.getReadableDatabase().rawQuery(
                "SELECT * FROM taikhoan WHERE tendn = ? AND matkhau = ?",
                new String[]{username, password});
        boolean isValid = cursor.getCount() > 0; // Kiểm tra xem có hàng nào không
        cursor.close(); // Đóng cursor để tránh rò rỉ bộ nhớ
        return isValid;
    }

    // Hàm lấy quyền người dùng
    private String getUserQuyen(String username) {
        String quyen = "";
        Cursor cursor = database.getReadableDatabase().rawQuery(
                "SELECT quyen FROM taikhoan WHERE tendn = ?",
                new String[]{username});

        if (cursor.moveToFirst()) {
            int quyenColumnIndex = cursor.getColumnIndex("quyen");
            if (quyenColumnIndex != -1) {
                quyen = cursor.getString(quyenColumnIndex);
            } else {
                Log.e("Error", "Column 'quyen' not found in result set");
            }
        } else {
            Log.e("Error", "No user found with username: " + username);
        }
        cursor.close(); // Đóng cursor
        return quyen;
    }

    // Hàm khởi động timer tự động đăng xuất
    private void startAutoLogoutTimer() {
        handler.removeCallbacks(timeoutRunnable); // Hủy bỏ bất kỳ Runnable nào trước đó

        timeoutRunnable = new Runnable() {
            @Override
            public void run() {
                // Đăng xuất người dùng
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false); // Đánh dấu là chưa đăng nhập
                editor.putString("tendn", null); // Xóa tên đăng nhập
                editor.apply();

                // Quay lại activity chính
                Intent intent = new Intent(Login_Activity.this, TrangchuNgdung_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        };

        handler.postDelayed(timeoutRunnable, TIMEOUT_DURATION); // Đặt thời gian để đăng xuất
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        startAutoLogoutTimer(); // Reset timer nếu có tương tác của người dùng
    }
}