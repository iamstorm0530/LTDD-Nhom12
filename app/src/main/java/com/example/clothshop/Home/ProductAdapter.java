package com.example.clothshop.Home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.clothshop.R;
import com.example.clothshop.model.Product;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.VH> {

    private final Context context;
    private final List<Product> original = new ArrayList<>();
    private final List<Product> display = new ArrayList<>();

    public ProductAdapter(Context context, List<Product> init) {
        this.context = context;
        setOriginalList(init);
    }

    public void setOriginalList(List<Product> list) {
        original.clear();
        if (list != null) original.addAll(list);

        display.clear();
        display.addAll(original);
        notifyDataSetChanged();
    }

    public void filterByCategory(String categoryId) {
        String target = categoryId == null ? "" : categoryId.trim();
        android.util.Log.d("AD", "filter target=" + target + " originalSize=" + original.size());

        display.clear();

        if ("ALL".equalsIgnoreCase(target)) {
            display.addAll(original);
        } else {
            for (Product p : original) {
                String c = (p.categoryId == null) ? "null" : p.categoryId.trim();
                if (c.equalsIgnoreCase(target)) {
                    display.add(p);
                }
            }
        }

        android.util.Log.d("AD", "displaySize=" + display.size());
        notifyDataSetChanged();
    }

    public void filter(List<Product> list) {
        display.clear();
        if (list != null) {
            display.addAll(list);
        }
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.home_item_product, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Product p = display.get(position);

        h.txtName.setText(p.name);

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        long priceVND = (long) p.price; // bạn đang lưu giá kiểu double; tự thống nhất đơn vị
        h.txtPrice.setText(formatter.format(priceVND) + "đ");

        h.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
            intent.putExtra("product", p); // product là item hiện tại
            v.getContext().startActivity(intent);
        });

        // Lấy ảnh đại diện: ưu tiên images[0]
        String imageUrl = null;
        if (p.images != null && !p.images.isEmpty()) imageUrl = p.images.get(0);

        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder) // nếu chưa có thì bạn tạo 1 ảnh placeholder.png
                .into(h.imgProduct);



    }

    @Override
    public int getItemCount() {
        return display.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtName, txtPrice;

        VH(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
        }
    }
}
