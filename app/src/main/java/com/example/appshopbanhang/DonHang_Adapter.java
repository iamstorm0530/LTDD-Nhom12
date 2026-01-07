package com.example.appshopbanhang;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class DonHang_Adapter extends ArrayAdapter<Order> {
    private Database database; // Khởi tạo đối tượng Database

    public DonHang_Adapter(Context context, List<Order> orders) {
        super(context, 0, orders);
        this.database = new Database(context, "banhang.db", null, 1); // Khởi tạo đúng cách
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ds_donhang, parent, false);
        }

        Order order = getItem(position);
        TextView txtMadh = convertView.findViewById(R.id.txtMahd);
        TextView txtTenKh = convertView.findViewById(R.id.txtTenKh);
        TextView txtDiaChi = convertView.findViewById(R.id.txtDiaChi);
        TextView txtSdt = convertView.findViewById(R.id.txtSdt);
        TextView txtTongThanhToan = convertView.findViewById(R.id.txtTongThanhToan);
        TextView txtNgayDatHang = convertView.findViewById(R.id.txtNgayDatHang);
        TextView txtTrangthai = convertView.findViewById(R.id.txtTrangthai);
        ImageButton sua = convertView.findViewById(R.id.imgsua);
        ImageButton next = convertView.findViewById(R.id.imgnext);

        // Hiển thị thông tin đơn hàng
        txtTenKh.setText(order.getTenKh());
        txtDiaChi.setText(order.getDiaChi());
        txtSdt.setText(order.getSdt());
        txtTongThanhToan.setText(String.valueOf(order.getTongTien()));
        txtNgayDatHang.setText(order.getNgayDatHang());
        txtTrangthai.setText(order.getTrangthai());
        txtMadh.setText(String.valueOf(order.getId()));

        next.setOnClickListener(view -> {
            if (order != null) {
                // Hiển thị Toast với ID đơn hàng
                Toast.makeText(getContext(), "ID đơn hàng: " + order.getId(), Toast.LENGTH_SHORT).show();

                // Gửi thông tin đơn hàng qua Intent
                Intent intent = new Intent(getContext(), ChiTietDonHang_Admin_Activity.class);
                intent.putExtra("donHangId", String.valueOf(order.getId())); // Đảm bảo rằng ID là chuỗi
                getContext().startActivity(intent);
            }
        });
        // Cập nhật trạng thái và kiểu hiển thị
        String trangThai = order.getTrangthai();
        setStatusTextStyle(txtTrangthai, trangThai);
        // Sự kiện cho nút "Sửa"
        sua.setOnClickListener(view1 -> showEditDialog(order));
        return convertView;
    }

    private void showEditDialog(Order order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.activity_sua_donhang, null);
        builder.setView(dialogView);

        // Các trường EditText
        TextView madh = dialogView.findViewById(R.id.id_dathang);
        EditText editTenKh = dialogView.findViewById(R.id.txtTenKh);
        EditText editDiaChi = dialogView.findViewById(R.id.txtDiaChi);
        EditText editSdt = dialogView.findViewById(R.id.txtSdt);
        EditText editTongThanhToan = dialogView.findViewById(R.id.txtTongThanhToan);
        EditText editNgayDatHang = dialogView.findViewById(R.id.txtNgayDatHang);
        TextView editTrangThai = dialogView.findViewById(R.id.txtTrangthai); // Thay đổi thành TextView

        // Điền dữ liệu hiện tại vào các trường
        madh.setText(String.valueOf(order.getId()));
        editTenKh.setText(order.getTenKh());
        editDiaChi.setText(order.getDiaChi());
        editSdt.setText(order.getSdt());
        editTongThanhToan.setText(String.valueOf(order.getTongTien()));
        editNgayDatHang.setText(order.getNgayDatHang());
        editTrangThai.setText(order.getTrangthai());

        // Xử lý sự kiện cho trường trạng thái
        editTrangThai.setOnClickListener(view -> {
            String[] statuses = {"Đang Chờ Duyệt","Đơn Hàng Bị Hủy", "Đang Chuẩn Bị Hàng", "Đã Giao Cho DVVC"};
            AlertDialog.Builder statusDialog = new AlertDialog.Builder(getContext());
            statusDialog.setTitle("Chọn Trạng Thái")
                    .setItems(statuses, (dialogInterface, i) -> {
                        editTrangThai.setText(statuses[i]); // Cập nhật trạng thái
                    })
                    .show();
        });

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            // Cập nhật thông tin đơn hàng
            updateDonHang(order, madh, editTenKh, editDiaChi, editSdt, editTongThanhToan, editNgayDatHang, editTrangThai);
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void setStatusTextStyle(TextView textView, String status) {
        switch (status) {
            case "Đang Chờ Duyệt":
                textView.setTextColor(getContext().getResources().getColor(android.R.color.holo_purple));
                break;
            case "Đang Chuẩn Bị Hàng":
                textView.setTextColor(getContext().getResources().getColor(android.R.color.holo_green_dark));
                break;
            case "Đã Giao Cho DVVC":
                textView.setTextColor(getContext().getResources().getColor(android.R.color.black));
                break;
            case "Đơn Hàng Bị Hủy":
                textView.setTextColor(getContext().getResources().getColor(android.R.color.holo_red_light));
                break;
            default:
                textView.setTextColor(getContext().getResources().getColor(android.R.color.black));
                break;
        }

        textView.setTypeface(null, android.graphics.Typeface.BOLD); // Đặt kiểu chữ đậm
    }
    private void updateDonHang(Order order, TextView madh, EditText editTenKh, EditText editDiaChi, EditText editSdt, EditText editTongThanhToan, EditText editNgayDatHang, TextView editTrangThai) {
        // Lấy dữ liệu mới từ các trường
        String newMadh = madh.getText().toString().trim();
        String newTenKh = editTenKh.getText().toString().trim();
        String newDiaChi = editDiaChi.getText().toString().trim();
        String newSdt = editSdt.getText().toString().trim();
        float newTongThanhToan = Float.parseFloat(editTongThanhToan.getText().toString().trim());
        String newNgayDatHang = editNgayDatHang.getText().toString().trim();
        String newTrangThai = editTrangThai.getText().toString().trim(); // Cập nhật trạng thái từ TextView

        // Cập nhật vào cơ sở dữ liệu
        SQLiteDatabase db = database.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tenkh", newTenKh);
        values.put("diachi", newDiaChi);
        values.put("sdt", newSdt);
        values.put("tongthanhtoan", newTongThanhToan);
        values.put("ngaydathang", newNgayDatHang);
        values.put("trangthai", newTrangThai); // Cập nhật trạng thái

        // Cập nhật dữ liệu
        db.update("Dathang", values, "id_dathang = ?", new String[]{String.valueOf(order.getId())});

        // Cập nhật đối tượng Order
        order.setTenKh(newTenKh);
        order.setDiaChi(newDiaChi);
        order.setSdt(newSdt);
        order.setTongTien(newTongThanhToan);
        order.setNgayDatHang(newNgayDatHang);
        order.setTrangthai(newTrangThai);

        notifyDataSetChanged(); // Cập nhật giao diện
    }
}