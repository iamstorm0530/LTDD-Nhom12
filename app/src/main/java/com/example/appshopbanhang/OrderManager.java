package com.example.appshopbanhang;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class OrderManager {
    private DatabaseHelper dbHelper;

    public OrderManager(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Phương thức để lấy tất cả chi tiết đơn hàng


    // Thêm đơn hàng mới
    public long addOrder(String tenKh, String diaChi, String sdt, float tongThanhToan) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tenkh", tenKh);
        values.put("diachi", diaChi);
        values.put("sdt", sdt);
        values.put("tongthanhtoan", tongThanhToan);

        values.put("trangthai", "Đang Chờ Duyệt");

        // Thêm đơn hàng vào bảng
        long id = db.insert("Dathang", null, values);

        // Kiểm tra xem việc thêm đơn hàng có thành công không
        if (id == -1) {
            Log.e("OrderManager", "Failed to insert order");
        }

        db.close();
        return id; // Trả về ID của đơn hàng vừa thêm
    }

    // Thêm chi tiết đơn hàng mới
    public long addOrderDetails(int idDonHang, String masp, int soluong, float dongia, byte[] anh) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id_dathang", idDonHang); // ID đơn hàng liên kết
        values.put("masp", masp);
        values.put("soluong", soluong);
        values.put("dongia", dongia);
        values.put("anh", anh); // Anh ở đây là kiểu byte[]

        long id = db.insert("Chitietdonhang", null, values);
        db.close();
        return id;
    }
}
