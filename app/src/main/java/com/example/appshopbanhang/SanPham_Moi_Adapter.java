package com.example.appshopbanhang;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

public class SanPham_Moi_Adapter extends RecyclerView.Adapter<SanPham_Moi_Adapter.SanPhamViewHolder> {

    private List<SanPham> productList;
    private OnItemClickListener listener;

    // Interface to handle click events
    public interface OnItemClickListener {
        void onItemClick(SanPham product);
    }

    // Constructor with listener parameter
    public SanPham_Moi_Adapter(List<SanPham> productList, OnItemClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SanPhamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ds_sp_moi, parent, false);
        return new SanPhamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SanPhamViewHolder holder, int position) {
        SanPham product = productList.get(position);

        // Display data
        holder.tensp.setText(product.getTensp());
        holder.mota.setText(product.getMota());
        holder.giatien.setText(String.valueOf(product.getDongia())); // Convert float to String
        holder.soluongkho.setText(String.valueOf(product.getSoluongkho())); // Convert float to String
        holder.ghichu.setText(product.getGhichu());
        holder.manhomsp.setText(product.getMansp()); // Convert float to String
        holder.masp.setText(product.getMasp()); // Convert float to String
        // Display image from blob
        setImageViewFromBlob(holder.img, product.getAnh());

        // Handle click event
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // Method to set image from blob
    private void setImageViewFromBlob(ImageView imageView, byte[] blob) {
        if (blob != null) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(blob);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.drawable.logo1); // Default image
        }
    }

    static class SanPhamViewHolder extends RecyclerView.ViewHolder {
        TextView tensp, ghichu, mota, giatien,masp,manhomsp,soluongkho;
        ImageView img;

        SanPhamViewHolder(View itemView) {
            super(itemView);
            tensp = itemView.findViewById(R.id.tensp);
            masp = itemView.findViewById(R.id.masp);
            manhomsp = itemView.findViewById(R.id.manhomsanpham);
            soluongkho = itemView.findViewById(R.id.soluongkho);
            ghichu = itemView.findViewById(R.id.ghichu);
            mota = itemView.findViewById(R.id.mota);
            giatien = itemView.findViewById(R.id.dongia);
            img = itemView.findViewById(R.id.imgsp);
        }
    }
}
