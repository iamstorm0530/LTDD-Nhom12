package com.example.clothshop.adapter.admin;

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

public class AdminProductAdapter
        extends RecyclerView.Adapter<AdminProductAdapter.ProductViewHolder> {

    private final List<Product> productList;
    private final List<Product> displayList = new ArrayList<>();

    public AdminProductAdapter(List<Product> productList) {
        this.productList = productList;
        this.displayList.addAll(productList);
    }

    // 🔥 FILTER (THÊM)
    public void filter(String keyword, String tag) {
        displayList.clear();

        for (Product p : productList) {
            boolean matchTag = tag.equals("ALL")
                    || (p.tag != null && p.tag.equalsIgnoreCase(tag));

            boolean matchSearch = p.name != null
                    && p.name.toLowerCase().contains(keyword.toLowerCase());

            if (matchTag && matchSearch) {
                displayList.add(p);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_product, parent, false);

        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ProductViewHolder holder, int position) {

        Product p = displayList.get(position);

        holder.tvName.setText(p.name);
        holder.tvPrice.setText(formatCurrency(p.price));
        holder.tvStock.setText("Stock: " + p.quantity);
        holder.tvRating.setText(
                p.averageRating + " ★ (" + p.reviewCount + ")"
        );

        if (p.tag != null && !p.tag.isEmpty()) {
            holder.tvTag.setText(p.tag.toUpperCase());
            holder.tvTag.setVisibility(View.VISIBLE);
        } else {
            holder.tvTag.setVisibility(View.GONE);
        }

        if ("hidden".equalsIgnoreCase(p.status)) {
            holder.tvStatus.setText("HIDDEN");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_chip_red);
        } else {
            holder.tvStatus.setText("ACTIVE");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_chip_green);
        }

        if (p.images != null && !p.images.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(p.images.get(0))
                    .centerCrop()
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.ic_launcher_foreground);
        }
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProduct;
        TextView tvName, tvPrice, tvStock, tvRating, tvTag, tvStatus;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvName     = itemView.findViewById(R.id.tvName);
            tvPrice    = itemView.findViewById(R.id.tvPrice);
            tvStock    = itemView.findViewById(R.id.tvStock);
            tvRating   = itemView.findViewById(R.id.tvRating);
            tvTag      = itemView.findViewById(R.id.tvTag);
            tvStatus   = itemView.findViewById(R.id.tvStatus);
        }
    }

    private String formatCurrency(double value) {
        return NumberFormat
                .getInstance(new Locale("vi", "VN"))
                .format(value) + " ₫";
    }
}
