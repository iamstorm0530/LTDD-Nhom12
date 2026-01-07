package com.example.appshopbanhang;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class ChiTietDonHangAdapter_NguoiDung extends ArrayAdapter<ChiTietDonHang> {
    private String tendn; // Biến để lưu tên đăng nhập

    public ChiTietDonHangAdapter_NguoiDung(Context context, List<ChiTietDonHang> details, String tendn) {
        super(context, 0, details);
        this.tendn = tendn; // Khởi tạo tên đăng nhập
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChiTietDonHang detail = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ds_chitietdonhang_nguoidung, parent, false);
        }

        TextView tvID_dathang = convertView.findViewById(R.id.txt_Iddathang);
        TextView tvMaSp = convertView.findViewById(R.id.txtMasp);
        TextView tvTenSp = convertView.findViewById(R.id.txtTensp);
        TextView tvSoLuong = convertView.findViewById(R.id.txtSoLuong);
        TextView tvDonGia = convertView.findViewById(R.id.txtGia);
        ImageView ivAnh = convertView.findViewById(R.id.imgsp);
        Button btndanhgia = convertView.findViewById(R.id.btndanhgia);
        TextView txtDanhgia = convertView.findViewById(R.id.textdanhgia);

        // Hiển thị ID đơn hàng
        tvID_dathang.setText(String.valueOf(detail.getId_dathang()));
        tvMaSp.setText(String.valueOf(detail.getMasp()));

        // Lấy tên sản phẩm từ DatabaseHelper
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        String tenSanPham = dbHelper.getTenSanPhamByMaSp(detail.getMasp());
        tvTenSp.setText(tenSanPham != null ? tenSanPham : "Không tìm thấy tên sản phẩm");

        // Hiển thị số lượng và đơn giá
        tvSoLuong.setText(String.valueOf(detail.getSoLuong()));
        tvDonGia.setText(String.valueOf(detail.getDonGia()));

        // Tải ảnh từ byte[]
        if (detail.getAnh() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(detail.getAnh(), 0, detail.getAnh().length);
            ivAnh.setImageBitmap(bitmap);
        } else {
            ivAnh.setImageResource(R.drawable.vest); // Hình ảnh mặc định
        }

        // Kiểm tra xem có đánh giá nào không
        boolean reviewExists = dbHelper.isReviewExists(String.valueOf(detail.getId_chitiet()));
        if (reviewExists) {
            btndanhgia.setVisibility(View.GONE); // Ẩn nút đánh giá nếu đã có đánh giá
            txtDanhgia.setVisibility(View.VISIBLE); // Hiển thị đánh giá
        } else {
            btndanhgia.setVisibility(View.VISIBLE); // Hiện nút đánh giá nếu chưa có đánh giá
            txtDanhgia.setVisibility(View.GONE); // Ẩn thông báo đánh giá
            btndanhgia.setOnClickListener(view -> showRatingDialog(detail.getMasp(), detail.getId_chitiet(), new RatingSubmissionListener() {
                @Override
                public void onRatingSubmitted() {
                    // Reload the data or refresh the UI here
                    notifyDataSetChanged(); // Refresh the list
                }
            }));
        }

        return convertView;
    }

    private void showRatingDialog(int masp, int idChitiet, RatingSubmissionListener listener) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.activity_danhgia_sanpham);

        ImageButton sao1 = dialog.findViewById(R.id.star1);
        ImageButton sao2 = dialog.findViewById(R.id.star2);
        ImageButton sao3 = dialog.findViewById(R.id.star3);
        ImageButton sao4 = dialog.findViewById(R.id.star4);
        ImageButton sao5 = dialog.findViewById(R.id.star5);
        EditText edtReview = dialog.findViewById(R.id.txtNoidungdanhgia);
        Button btnSubmit = dialog.findViewById(R.id.btnSave);

        final int[] starCount = {0};

        ImageButton[] stars = {sao1, sao2, sao3, sao4, sao5};
        for (int i = 0; i < stars.length; i++) {
            final int index = i;
            stars[i].setOnClickListener(v -> {
                setStarRating(stars, index);
                starCount[0] = index + 1;
            });
            stars[i].setOnLongClickListener(v -> {
                resetStarRating(stars, index);
                if (starCount[0] > index) {
                    starCount[0]--;
                }
                return true;
            });
        }

        btnSubmit.setOnClickListener(v -> {
            if (starCount[0] > 0 && !edtReview.getText().toString().isEmpty()) {
                // Tạo các giá trị sao
                String sao11 = (starCount[0] >= 1) ? "star2" : "star1"; // Sao 1
                String sao22 = (starCount[0] >= 2) ? "star2" : "star1"; // Sao 2
                String sao33 = (starCount[0] >= 3) ? "star2" : "star1"; // Sao 3
                String sao44 = (starCount[0] >= 4) ? "star2" : "star1"; // Sao 4
                String sao55 = (starCount[0] >= 5) ? "star2" : "star1"; // Sao 5

                // Tạo đối tượng DanhGiaSanPham
                DanhGiaSanPham danhGia = new DanhGiaSanPham(null, String.valueOf(masp), String.valueOf(idChitiet),
                        edtReview.getText().toString(), tendn, sao11, sao22, sao33, sao44, sao55);

                // Ghi dữ liệu vào cơ sở dữ liệu
                DatabaseHelper dbHelper = new DatabaseHelper(getContext());
                dbHelper.insertDanhGia(danhGia);
                Toast.makeText(getContext(), "Đánh giá thành công", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
                if (listener != null) {
                    listener.onRatingSubmitted(); // Call the listener
                }
            } else {
                if (starCount[0] == 0) {
                    Toast.makeText(getContext(), "Vui lòng chọn ít nhất một sao.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Vui lòng nhập nội dung đánh giá.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private void setStarRating(ImageButton[] stars, int index) {
        // Đặt trạng thái cho các sao
        for (int i = 0; i <= index; i++) {
            stars[i].setImageResource(R.drawable.star2); // Sao vàng
        }
        for (int i = index + 1; i < stars.length; i++) {
            stars[i].setImageResource(R.drawable.star1); // Sao rỗng
        }
    }

    private void resetStarRating(ImageButton[] stars, int index) {
        // Đặt sao tại vị trí index về sao rỗng
        stars[index].setImageResource(R.drawable.star1); // Sao rỗng
    }

    private class LoadImage extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        public LoadImage(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream input = new URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                imageView.setImageBitmap(result);
            }
        }
    }

    public interface RatingSubmissionListener {
        void onRatingSubmitted();
    }
}