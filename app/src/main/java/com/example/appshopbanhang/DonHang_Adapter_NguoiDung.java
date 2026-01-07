package com.example.appshopbanhang;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
public class DonHang_Adapter_NguoiDung extends ArrayAdapter<Order> {
    public DonHang_Adapter_NguoiDung(Context context, List<Order> orders) {
        super(context, 0, orders);
    }

    @Override

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.ds_donhang_nguoidung, parent, false);
            }

            Order order = getItem(position);
        TextView txtMadh = convertView.findViewById(R.id.txtMahd);
            TextView txtTenKh = convertView.findViewById(R.id.txtTenKh);
            TextView txtDiaChi = convertView.findViewById(R.id.txtDiaChi);
            TextView txtSdt = convertView.findViewById(R.id.txtSdt);
            TextView txtTongThanhToan = convertView.findViewById(R.id.txtTongThanhToan);
            TextView txtNgayDatHang = convertView.findViewById(R.id.txtNgayDatHang);
        TextView txtTrangthai = convertView.findViewById(R.id.txtTrangthai);
        ImageButton next = convertView.findViewById(R.id.imgnext);


        txtTenKh.setText(order.getTenKh());
            txtDiaChi.setText(order.getDiaChi());
            txtSdt.setText(order.getSdt());
            txtTongThanhToan.setText(String.valueOf(order.getTongTien()));
            txtNgayDatHang.setText(order.getNgayDatHang());
        txtTrangthai.setText(order.getTrangthai());
        txtMadh.setText(String.valueOf(order.getId()));
        // Xử lý sự kiện cho nút "next"
        // Xử lý sự kiện cho nút "next"
        next.setOnClickListener(view -> {
            if (order != null) {
                // Hiển thị Toast với ID đơn hàng
                Toast.makeText(getContext(), "ID đơn hàng: " + order.getId(), Toast.LENGTH_SHORT).show();

                // Kiểm tra trạng thái đơn hàng
                String trangThai = order.getTrangthai();
                Intent intent;
                if ("Đã Giao Cho DVVC".equals(trangThai)) {
                    intent = new Intent(getContext(), ChiTietDonHang_NguoiDung_Activity.class);
                } else {
                    intent = new Intent(getContext(), ChiTietDonHang_NguoiDung_3_TrangThai_Khac.class);
                }
                intent.putExtra("donHangId", String.valueOf(order.getId())); // Đảm bảo rằng ID là chuỗi
                getContext().startActivity(intent);
            }
        });
        // Cập nhật trạng thái và kiểu hiển thị
        String trangThai = order.getTrangthai();
        setStatusTextStyle(txtTrangthai, trangThai);
            return convertView;
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
}
