package com.example.appshopbanhang;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class DanhGiaSanPham_Adapter extends BaseAdapter {

    private Context context;
    private Uri selectedImageUri; // Biến lưu trữ URI đã chọn
    private static final int REQUEST_CODE_PICK_IMAGE = 1; // Định nghĩa mã yêu cầu
    private ArrayList<DanhGiaSanPham> spList;
    private boolean showFullDetails; // Biến để xác định xem có hiển thị 7 thuộc tính hay không
    private Database database;

    public DanhGiaSanPham_Adapter(Context context, ArrayList<DanhGiaSanPham> bacsiList, boolean showFullDetails) {
        this.context = context;
        this.spList = bacsiList;
        this.showFullDetails = showFullDetails; // Khởi tạo biến
        this.database = new Database(context, "banhang.db", null, 1);
    }

    @Override
    public int getCount() {
        return spList.size();
    }

    @Override
    public Object getItem(int position) {
        return spList.get(position);
    }

    public void setSelectedImageUri(Uri uri) {
        this.selectedImageUri = uri; // Setter để cập nhật URI
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (showFullDetails) {
            return getViewWith8Properties(position, convertView, parent);
        } else {
            return getViewWith4Properties(position, convertView, parent);
        }
    }




    public View getViewWith8Properties(int i, View view, ViewGroup parent) {
        View viewtemp;
        if (view == null) {
            viewtemp = LayoutInflater.from(parent.getContext()).inflate(R.layout.ds_danhgiasanpham, parent, false);
        } else {
            viewtemp = view;
        }

        DanhGiaSanPham tt = spList.get(i);
        TextView masp = viewtemp.findViewById(R.id.masp);
        TextView id_chitiet = viewtemp.findViewById(R.id.id_chitiet);
        TextView id_danhgia = viewtemp.findViewById(R.id.id_danhgia);
        EditText noidung = viewtemp.findViewById(R.id.txtNoidungdanhgia);
        TextView tendn = viewtemp.findViewById(R.id.tenkhachhang);
        ImageButton star1 = viewtemp.findViewById(R.id.star1);
        ImageButton star2 = viewtemp.findViewById(R.id.star2);
        ImageButton star3 = viewtemp.findViewById(R.id.star3);
        ImageButton star4 = viewtemp.findViewById(R.id.star4);
        ImageButton star5 = viewtemp.findViewById(R.id.star5);

        // Hiển thị thông tin sản phẩm
        masp.setText(tt.getMasp());
        id_chitiet.setText(tt.getId_chitiet());
        id_danhgia.setText(tt.getId_danhgia());
        noidung.setText(tt.getNoidung());
        tendn.setText(tt.getTendn());

        // Hiển thị sao dựa trên tên đã lưu trong cơ sở dữ liệu
        setStarImage(star1, tt.getSao1());
        setStarImage(star2, tt.getSao2());
        setStarImage(star3, tt.getSao3());
        setStarImage(star4, tt.getSao4());
        setStarImage(star5, tt.getSao5());

        return viewtemp;
    }

    private void setStarImage(ImageButton starButton, String saoName) {
        int resourceId = context.getResources().getIdentifier(saoName, "drawable", context.getPackageName());
        if (resourceId != 0) {
            starButton.setImageResource(resourceId); // Set hình ảnh sao
        } else {
            starButton.setImageResource(R.drawable.star2); // Hình ảnh mặc định nếu không tìm thấy
        }
    }
    public View getViewWith4Properties(int i, View view, ViewGroup parent) {
        View viewtemp;
        if (view == null) {
            viewtemp = LayoutInflater.from(parent.getContext()).inflate(R.layout.ds_hienthi_gridview1_nguoidung, parent, false);
        } else {
            viewtemp = view;
        }



        return viewtemp;
    }


}
