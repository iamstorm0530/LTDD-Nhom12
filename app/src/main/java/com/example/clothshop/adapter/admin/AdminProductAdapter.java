package com.example.clothshop.adapter.admin;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.clothshop.R;
import com.example.clothshop.model.Product;
import com.example.clothshop.model.Variant;

import java.text.NumberFormat;
import java.util.*;

public class AdminProductAdapter
        extends RecyclerView.Adapter<AdminProductAdapter.ProductViewHolder> {

    private final List<Product> productList;
    private final List<Product> displayList = new ArrayList<>();

    // ✅ THỨ TỰ SIZE CỐ ĐỊNH
    private static final List<String> SIZE_ORDER =
            Arrays.asList("S", "M", "L");

    public AdminProductAdapter(List<Product> productList) {
        this.productList = productList;
        displayList.addAll(productList);
    }

    public void filter(String keyword, String tag) {
        displayList.clear();
        for (Product p : productList) {
            boolean matchTag = tag.equals("ALL")
                    || (p.getTag() != null && p.getTag().equalsIgnoreCase(tag));
            boolean matchSearch = p.getName()
                    .toLowerCase().contains(keyword.toLowerCase());

            if (matchTag && matchSearch) displayList.add(p);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_product, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ProductViewHolder h, int pos) {

        Product p = displayList.get(pos);

        h.tvName.setText(p.getName());
        h.tvPrice.setText(formatCurrency(p.getPrice()));
        h.tvStock.setText("Stock: " + p.getTotalQuantity());
        h.tvRating.setText(
                p.getAverageRating() + " ★ (" + p.getReviewCount() + ")"
        );

        // ===== TAG =====
        h.tvTag.setText(p.getTag().toUpperCase());

        // ===== STATUS (🔥 FIX Ở ĐÂY) =====
        String status = p.getStatus();
        h.tvStatus.setText(status.toUpperCase());

        if ("hidden".equalsIgnoreCase(status)) {
            h.tvStatus.setBackgroundResource(R.drawable.bg_chip_red);
            h.tvStatus.setTextColor(Color.RED);
        } else {
            h.tvStatus.setBackgroundResource(R.drawable.bg_chip_green);
            h.tvStatus.setTextColor(Color.GREEN);
        }

        // ===== IMAGE =====
        if (!p.getImages().isEmpty()) {
            Glide.with(h.itemView.getContext())
                    .load(p.getImages().get(0))
                    .centerCrop()
                    .into(h.imgProduct);
        }

        // 🔥 CLEAR VARIANTS CŨ
        h.layoutVariants.removeAllViews();

        // 🔥 GROUP VARIANTS THEO COLOR
        Map<String, Map<String, Variant>> colorMap = new LinkedHashMap<>();

        for (Variant v : p.getVariants()) {
            colorMap
                    .computeIfAbsent(v.getColor(), k -> new HashMap<>())
                    .put(v.getSize(), v);
        }

        // 🔥 RENDER TỪNG COLOR
        for (String color : colorMap.keySet()) {
            h.layoutVariants.addView(
                    createColorRow(
                            h.itemView.getContext(),
                            color,
                            colorMap.get(color)
                    )
            );
        }
    }

    // ================= COLOR ROW =================
    private View createColorRow(
            Context ctx,
            String color,
            Map<String, Variant> sizeMap) {

        LinearLayout row = new LinearLayout(ctx);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 6, 0, 6);

        // COLOR NAME
        TextView tvColor = new TextView(ctx);
        tvColor.setText(color.toUpperCase());
        tvColor.setWidth(dp(ctx, 48));
        tvColor.setTextSize(12);
        tvColor.setTextColor(Color.parseColor("#1C1C1E"));

        row.addView(tvColor);

        LinearLayout sizeWrap = new LinearLayout(ctx);
        sizeWrap.setOrientation(LinearLayout.HORIZONTAL);

        // ✅ S → M → L
        for (String size : SIZE_ORDER) {
            Variant v = sizeMap.get(size);
            if (v == null) continue;

            TextView chip = new TextView(ctx);
            chip.setText(size);
            chip.setTextSize(11);
            chip.setPadding(12, 6, 12, 6);

            int qty = v.getQuantity();

            if (qty == 0) {
                chip.setBackgroundResource(R.drawable.bg_size_red);
                chip.setTextColor(Color.WHITE);
            } else if (qty < 30) {
                chip.setBackgroundResource(R.drawable.bg_chip_yellow);
                chip.setTextColor(Color.BLACK);
            } else {
                chip.setBackgroundResource(R.drawable.bg_size_green);
                chip.setTextColor(Color.WHITE);
            }

            sizeWrap.addView(chip);
        }

        row.addView(sizeWrap);
        return row;
    }

    private int dp(Context c, int v) {
        return (int) (v * c.getResources()
                .getDisplayMetrics().density);
    }

    private String formatCurrency(double v) {
        return NumberFormat
                .getInstance(new Locale("vi", "VN"))
                .format(v) + " ₫";
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    // ================= VIEW HOLDER =================
    static class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProduct;
        TextView tvName, tvPrice, tvStock, tvRating, tvTag, tvStatus;
        LinearLayout layoutVariants;

        ProductViewHolder(@NonNull View v) {
            super(v);
            imgProduct = v.findViewById(R.id.imgProduct);
            tvName = v.findViewById(R.id.tvName);
            tvPrice = v.findViewById(R.id.tvPrice);
            tvStock = v.findViewById(R.id.tvStock);
            tvRating = v.findViewById(R.id.tvRating);
            tvTag = v.findViewById(R.id.tvTag);
            tvStatus = v.findViewById(R.id.tvStatus);
            layoutVariants = v.findViewById(R.id.layoutVariants);
        }
    }
    // ================= GET ITEM FOR CLICK =================
    public Product getItemAt(int position) {
        if (position < 0 || position >= displayList.size()) {
            return null;
        }
        return displayList.get(position);
    }

}
